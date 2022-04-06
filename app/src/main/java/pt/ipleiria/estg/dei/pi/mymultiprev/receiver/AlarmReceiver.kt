package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.AlarmDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Alarm
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.service.AlarmService
import pt.ipleiria.estg.dei.pi.mymultiprev.service.RingtoneService
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainActivity
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.RandomIntUtil
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver :
    BroadcastReceiver() {
    @Inject
    lateinit var prescriptionItemsRepository: PrescriptionItemsRepository

    @Inject
    lateinit var drugRepository: DrugRepository

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    @Inject
    lateinit var alarmDao: AlarmDao

    override fun onReceive(context: Context, intent: Intent) {
        //get the time when the alarm was scheduled
        val wakeUpTimeInMillis = intent.getLongExtra(Constants.EXTRACT_ALARM_TIME, 0L)

        GlobalScope.launch {
            val currentAlarmPrescriptionItems: LinkedList<PrescriptionItem> = LinkedList()
            var nextAlarmTimestamp: Long = Long.MAX_VALUE
            var isExact = false
            //verifies if the alarm is supposed to still be triggered

            val prescriptionItems =
                prescriptionItemsRepository.getActivePrescriptionItems(sharedPreferencesRepository.getCurrentPatientId())
                    .first().data
            if (!prescriptionItems.isNullOrEmpty()) {
                prescriptionItems.forEach lit@{ prescriptionItem ->
                    val nextIntake = prescriptionItem.nextIntake
                    if (nextIntake != null && prescriptionItem.alarm) {
                        val nextIntakeMillis =
                            nextIntake.toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds()
                        //if there's still alarms to be triggered in this timeInMillis, drug(s) are added to currentAlarm list (multiple drugs can be assigned to one unique alarm)
                        if (nextIntakeMillis == wakeUpTimeInMillis) {
                            isExact = true
                        }
                        if (nextIntakeMillis <= wakeUpTimeInMillis) {
                            currentAlarmPrescriptionItems.add(prescriptionItem)
                            return@lit
                        }
                        //find when's the next alarm
                        if (nextIntakeMillis in (wakeUpTimeInMillis + 1)..nextAlarmTimestamp) {
                            nextAlarmTimestamp = nextIntakeMillis
                        }
                    }
                }
            }

            manageNextAlarm(nextAlarmTimestamp, context)

            //if there's still assigned alarm's to this broadcast. we should trigger the notification
            if (currentAlarmPrescriptionItems.isNotEmpty() && isExact) {
                //multiple drugs

                if (currentAlarmPrescriptionItems.size > 1) {
                    var drugsAux = ""
                    var count = 0
                    currentAlarmPrescriptionItems.forEach { prescriptionItem ->
                        count++
                        val drugName =
                            drugRepository.getDrugById(prescriptionItem.drug).first().data?.name
                        if (currentAlarmPrescriptionItems.size != count && count != 1) {
                            drugsAux += ", "
                        }
                        if (currentAlarmPrescriptionItems.size == count) {
                            drugsAux += " e "
                        }
                        if (drugName != null) {
                            drugsAux += drugName
                        }

                    }
                    buildNotification(
                        context,
                        context.getString(R.string.taking_medicines),
                        context.getString(R.string.time_to_take_medicines) + drugsAux
                    )
                    //single drug
                } else {
                    val drugName = drugRepository.getDrugById(currentAlarmPrescriptionItems[0].drug)
                        .first().data?.name
                    buildNotification(
                        context,
                        context.getString(R.string.taking_medicin),
                        context.getString(R.string.time_to_take_medicin) + drugName
                    )
                }
                val i = Intent(context, RingtoneService::class.java)
                context.startService(i)
            }
        }
    }

    private suspend fun manageNextAlarm(nextAlarmTimestamp: Long, context: Context) {
        if (nextAlarmTimestamp == Long.MAX_VALUE) {
            sharedPreferencesRepository.removeAlarm()
            return
        }
        val currentAlarms = alarmDao.getAlarms()
        val nextAlarmLocalDateTime = Instant.fromEpochMilliseconds(
            nextAlarmTimestamp
        ).toLocalDateTime(Constants.TIME_ZONE)
        if (currentAlarms.find {
                it.alarm == nextAlarmLocalDateTime
            } == null) {
            AlarmService(context).setExactAlarm(nextAlarmTimestamp)

            alarmDao.addAlarm(Alarm(0, nextAlarmLocalDateTime))
        }
        sharedPreferencesRepository.saveAlarm(nextAlarmTimestamp)
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        val notificationID = RandomIntUtil.getRandomInt()
        // Create an Intent for the activity you want to start
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.putExtra(Constants.NOTIFICATION_ID, notificationID)
        // Create the TaskStackBuilder
        val mainActivityPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(mainActivityIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(RandomIntUtil.getRandomInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val pauseIntent = Intent(context, RingtoneService::class.java)
        pauseIntent.putExtra(Constants.NOTIFICATION_ID, notificationID)
        pauseIntent.action = Constants.ACTION_PAUSE
        val pausePendingIntent = PendingIntent.getService(
            context,
            RandomIntUtil.getRandomInt(), pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainActivityPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(
                R.drawable.ic_baseline_alarm_on_24,
                context.getString(R.string.confirm),
                mainActivityPendingIntent
            )
            .addAction(
                R.drawable.ic_baseline_alarm_off_24,
                context.getString(R.string.stop),
                pausePendingIntent
            )
        with(NotificationManagerCompat.from(context)) {
            notify(notificationID, builder.build())
        }
    }
}