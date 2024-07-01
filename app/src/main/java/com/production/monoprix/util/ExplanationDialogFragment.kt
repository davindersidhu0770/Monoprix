package com.production.monoprix.util

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.production.monoprix.R

class ExplanationDialogFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(): ExplanationDialogFragment {
            return ExplanationDialogFragment()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val customDialogView = inflater.inflate(R.layout.custom_alert_title, null)

        val customTitleTextView = customDialogView.findViewById<TextView>(R.id.customTitleTextView)
        val customMessageTextView =
            customDialogView.findViewById<TextView>(R.id.customMessageTextView)
        val customSkipButton = customDialogView.findViewById<TextView>(R.id.customSkipButton)
        val customAllowButton = customDialogView.findViewById<TextView>(R.id.customAllowButton)

        customTitleTextView.text = getString(R.string.notificationtitle)
        customMessageTextView.text = getString(R.string.notificationmessage)

        builder.setView(customDialogView).setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        customSkipButton.setOnClickListener {
            alertDialog.dismiss()
        }

        customAllowButton.setOnClickListener {
            alertDialog.dismiss()
            navigateToAppSettings()
        }

        return alertDialog
    }

    private fun navigateToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", "com.production.monoprix", null)
        intent.data = uri
        startActivity(intent)
    }

}
