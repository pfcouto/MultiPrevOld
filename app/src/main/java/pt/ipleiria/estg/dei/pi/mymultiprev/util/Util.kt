package pt.ipleiria.estg.dei.pi.mymultiprev.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource.Error
import java.time.format.DateTimeFormatter

object Util {
    fun formatDateTime(date: LocalDateTime): String {
        return date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
    }

    fun <T> Fragment.handleError(error: Error<T>) {
        when {
            error.isNetworkError -> requireView().snackBar("Por favor, verifique a sua conexão á internet")
            else -> requireView().snackBar("Ocorreu um erro")
        }
    }

    private fun View.snackBar(message: String) {
        val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}