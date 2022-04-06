package pt.ipleiria.estg.dei.pi.mymultiprev.util

import android.Manifest
import kotlinx.datetime.TimeZone

object Constants {
    // User Preferences
    const val MY_PREFS = "MY_PREFS"
    const val USER_PREFERENCES = "USER_PREFERENCES"
    const val ACTIVE_PRESCRIPTION_ITEMS_LAYOUT = "ACTIVE_PRESCRIPTION_ITEMS_LAYOUT"

    // Auth
    const val AUTH_SP = "AUTH_SP"
    const val FIRST_TIME_LOGIN = "FIRST_TIME_LOGIN"
    const val PATIENT_ID = "PATIENT_ID"
    const val AUTH_TOKEN = "AUTH_TOKEN"

    // Alarm
    const val ACTION_SET_EXACT_ALARM = "ACTION_SET_EXACT_ALARM"
    const val EXTRACT_ALARM_TIME = "EXTRACT_ALARM_TIME"
    const val ACTION_PAUSE = "ACTION_PAUSE"
    const val NOTIFICATIONS_CHANNEL_ID = "CHANNEL_ID"
    const val SP_NEXT_ALARM = "NEXT_ALARM"
    const val NOTIFICATION_ID = "NOTIFICATION_ID"
    const val SP_DEFAULT_LONG = -1L
    const val ALARM_RING_TIME = 30000L

    // API
    const val API_BASE_URL = "http://172.22.21.2:80/api/"

    // Crypto
    const val SHA3_SIZE = 256

    // Local Database
    const val LOCAL_DATABASE_NAME = "multiprev_database"

    // Prescription Item Image Transition
    const val PRESCRIPTION_ITEM_ID = "PRESCRIPTION_ITEM_ID"
    const val PRESCRIPTION_ITEM_IMAGE_TRANSITION = "PRESCRIPTION_ITEM_IMAGE_TRANSITION"

    // Permissions
    const val REQUEST_CODE_PERMISSIONS = 10
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    // Camera
    const val CAMERA_ACTIVITY_RESULT = 1

    // Time
    val TIME_ZONE = TimeZone.currentSystemDefault()
    const val HOURS_OF_DAY = 24

    // Date Format
    const val DATE_FORMAT = "dd/MM/yyyy HH:mm"
}