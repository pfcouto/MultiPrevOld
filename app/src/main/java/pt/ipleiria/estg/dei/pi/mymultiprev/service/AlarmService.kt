package pt.ipleiria.estg.dei.pi.mymultiprev.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import pt.ipleiria.estg.dei.pi.mymultiprev.receiver.AlarmReceiver
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.RandomIntUtil

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long) {
        setAlarm(timeInMillis, getPendingIntent(getIntent().apply {
            action = Constants.ACTION_SET_EXACT_ALARM
            putExtra(Constants.EXTRACT_ALARM_TIME, timeInMillis)
        }))
    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            //assure the exact alarm during the doze mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            RandomIntUtil.getRandomInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}