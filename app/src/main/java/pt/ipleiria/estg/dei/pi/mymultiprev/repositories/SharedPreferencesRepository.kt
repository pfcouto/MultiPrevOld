package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val spAlarmService =
        context.getSharedPreferences(Constants.MY_PREFS, Context.MODE_PRIVATE)
    private val spAlarmServiceEditor = spAlarmService.edit()

    private val spAuth = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)
    private val spAuthEditor = spAuth.edit()

    private val spUserPreferences =
        context.getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
    private val spUserPreferencesEditor = spUserPreferences.edit()

    //Alarm
    fun saveAlarm(nextAlarm: Long) {
        spAlarmServiceEditor.putLong(Constants.SP_NEXT_ALARM, nextAlarm)
        spAlarmServiceEditor.apply()
    }

    fun getAlarm() = spAlarmService.getLong(Constants.SP_NEXT_ALARM, Constants.SP_DEFAULT_LONG)

    fun removeAlarm() {
        spAlarmServiceEditor.remove(Constants.SP_NEXT_ALARM)
        spAlarmServiceEditor.apply()
    }

    //Auth
    fun getCurrentPatientId() = spAuth.getString(Constants.PATIENT_ID, "")!!

    fun getCurrentActivePrescriptionItemLayoutPreference() =
        spUserPreferences.getBoolean(Constants.ACTIVE_PRESCRIPTION_ITEMS_LAYOUT, false)

    fun setCurrentActivePrescriptionItemLayoutPreference(pref: Boolean) =
        spUserPreferencesEditor.putBoolean(Constants.ACTIVE_PRESCRIPTION_ITEMS_LAYOUT, pref).apply()

    fun saveToken(token: String) {
        spAuthEditor.putString(Constants.AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? = spAuth.getString(Constants.AUTH_TOKEN, null)

    fun isLoggedIn(): Boolean = spAuth.getBoolean(Constants.FIRST_TIME_LOGIN, false)

    fun deleteAll() {
        spAuthEditor.clear().commit()
        spAlarmServiceEditor.clear().commit()
        spUserPreferencesEditor.clear().commit()
    }

}