package com.example.chatappfirebase.ui.common



import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.chatappfirebase.R

class LoadingDialog( val activity: Context) {
    private var dialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)

        builder.setView(inflater.inflate(R.layout.layout_dialog_loading, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}
