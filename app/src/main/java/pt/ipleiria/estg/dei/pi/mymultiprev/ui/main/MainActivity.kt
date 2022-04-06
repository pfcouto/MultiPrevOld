package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.service.RingtoneService
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginActivity
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val viewModel: MainViewModel by viewModels()
    private lateinit var buttonLogout: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disableNightMode()

        setContentView(R.layout.activity_main)

        buttonLogout = findViewById(R.id.buttonLogout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        createNotificationChannel()
        dismissNotifications()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(
            navController
        )



        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val labelFragmentTitle = findViewById<TextView>(R.id.textViewFragmentTitle)
            labelFragmentTitle.visibility =
                View.VISIBLE
            buttonLogout.visibility = View.GONE
            Log.i(TAG, "Destination = ${destination.id}")
            when (destination.id) {
                R.id.activeDrugListFragment -> {
                    buttonLogout.visibility = View.VISIBLE
                    labelFragmentTitle.text = getString(R.string.active_prescription_items)
                }
                R.id.registerSymptomsFragment ->
                    labelFragmentTitle.visibility = View.GONE
                R.id.medicineHistoryFragment ->
                    labelFragmentTitle.text = getString(R.string.medicine_history)
                R.id.confirmAcquisitionFragment -> labelFragmentTitle.text =
                    getString(R.string.confirm_acquisition)
                R.id.newIntakeDetailsFragment ->
                    labelFragmentTitle.text = getString(R.string.confirm_intake)
            }
        }

        bottomNavigationView.apply {
            setOnNavigationItemSelectedListener {
                if (!viewModel.isNetworkAvailable() && it.itemId == R.id.registerSymptomsFragment) {
                    Snackbar.make(
                        this,
                        R.string.no_internet_connection,
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(getString(R.string.OK)) {}.show()
                    return@setOnNavigationItemSelectedListener false
                }
                navController.navigate(it.itemId)
                true
            }

            setOnNavigationItemReselectedListener {
                if (navController.currentDestination!!.id == R.id.activeDrugListFragment) {
                    // Do nothing to ignore the reselection
                } else {
                    navController.navigate(it.itemId)
                }

            }
        }

        buttonLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.logout_title_dialog))
            builder.setMessage(getString(R.string.logout_message_dialog))
                .setPositiveButton(R.string.yes) { dialog, id ->
                    viewModel.deleteAppData()
                }
                .setNegativeButton(R.string.no) { dialog, id ->
                    dialog.dismiss()
                }
            builder.show()
        }


        viewModel.isAllClear.observe(this) {
            if (it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = getString(R.string.channel_name)
            val name = "notifications_channel"
            //val descriptionText = getString(R.string.channel_description)
            val descriptionText = "Channel for Alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(Constants.NOTIFICATIONS_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun dismissNotifications() {
        val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
        if (notificationId == -1) {
            return
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
        val intent = Intent(this, RingtoneService::class.java)
        intent.action = Constants.ACTION_PAUSE
        startService(intent)
    }

}