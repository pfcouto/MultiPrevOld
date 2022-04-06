package pt.ipleiria.estg.dei.pi.mymultiprev.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.CountDownTimer
import android.os.IBinder
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants.ACTION_PAUSE

class RingtoneService : Service() {
    private lateinit var ringtone: Ringtone

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_REDELIVER_INTENT;
        }
        val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
        if (ACTION_PAUSE == intent.action) {
            val i = Intent(this, RingtoneService::class.java)
            stopService(i)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        } else {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, uri)
            ringtone.play()
            object : CountDownTimer(Constants.ALARM_RING_TIME, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //nothing to do here
                }

                override fun onFinish() {
                    ringtone.stop()
                }

            }.start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (this::ringtone.isInitialized && ringtone.isPlaying) {
            ringtone.stop()
        }
    }
}