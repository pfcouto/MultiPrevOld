package pt.ipleiria.estg.dei.pi.mymultiprev.util

import android.app.Activity
import android.app.AlertDialog
import pt.ipleiria.estg.dei.pi.mymultiprev.R

class LoadingDialog(private val activity: Activity) {
    private lateinit var alertDialog: AlertDialog
    private var isShowing = false

    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog,null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        isShowing=true
    }

    fun dismissDialog(){
        if(isShowing){
            alertDialog.dismiss()
        }
    }
}