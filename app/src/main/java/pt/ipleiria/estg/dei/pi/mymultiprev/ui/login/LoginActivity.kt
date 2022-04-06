package pt.ipleiria.estg.dei.pi.mymultiprev.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.ActivityLoginBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainActivity
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disableNightMode()

        Log.i(TAG, "### Login Created ###")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        viewModel.apply {
            checkIsLoggedIn()
            treatLoginResponse()
        }

        binding.apply {
            loginForm.buttonLogin.setOnClickListener {
                val username = loginForm.editTextUsername.text.toString().trim()
                val password = loginForm.editTextPassword.text.toString().trim()
                Log.i(TAG, "Button Login Clicked: $username / $password")

                if (username.isEmpty()) {
                    loginForm.textInputLayoutUsername.error =
                        getString(R.string.login_empty_username)
                    return@setOnClickListener
                } else {
                    loginForm.textInputLayoutUsername.isErrorEnabled = false
                }

                if (password.isEmpty()) {
                    loginForm.textInputLayoutPassword.error =
                        getString(R.string.login_empty_password)
                    return@setOnClickListener
                } else {
                    loginForm.textInputLayoutPassword.isErrorEnabled = false
                }

                viewFlipper.showNext()
                viewModel.login(
                    username,
                    password
                )
            }
        }
    }

    private fun LoginViewModel.treatLoginResponse() {
        loginResponse.observe(this@LoginActivity) { loginResponseResource ->
            when (loginResponseResource) {
                is Resource.Success -> {
                    Log.i(TAG, "loginResponseResource is Resource.Success")
                    treatSuccessResponse()
                }
                is Resource.Error -> {
                    Log.i(TAG, "loginResponseResource is Resource.Error")
                    if (loginResponseResource.error is HttpException) treatHTTPException(
                        loginResponseResource.error.code()
                    ) else {
                        treatErrorResponse()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun treatHTTPException(errorCode: Int) {
        binding.viewFlipper.showPrevious()
        Log.i(TAG, "HTTP Exception - $errorCode")
        when (errorCode) {
            HTTP_UNAUTHORIZED -> {
                binding.loginForm.textViewErrors.apply {
                    text = getString(R.string.login_invalid_credentials)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun treatErrorResponse() {
        Log.i(TAG, "Timeout while connecting to API")
        binding.viewFlipper.showPrevious()
        val errorMsg = if (!viewModel.isNetworkAvailable())
            getString(R.string.login_no_internet)
        else
            getString(R.string.login_connection_timeout)

        Log.i(TAG, "Internet Connection - $viewModel.isNetworkAvailable()")

        Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.OK)) {}.show()
    }

    private fun treatSuccessResponse() {
        viewModel.apply {
            savePatientId()
            isLoggedIn = true
        }

        viewModel.patient.observe(this) {
            if (it.data != null) {
                Log.i(TAG, "Logged In Successfully!")
                Log.i(TAG, "### Login Destroyed ###")
                openMainActivity()
                finish()
            }
        }
    }

    private fun LoginViewModel.checkIsLoggedIn() {
        Log.i(TAG, "LoginViewModel.checkIsLoggedIn() - $isLoggedIn")
        if (isLoggedIn) {
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}