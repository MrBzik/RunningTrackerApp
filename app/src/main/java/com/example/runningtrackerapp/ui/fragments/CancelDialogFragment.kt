package com.example.runningtrackerapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningtrackerapp.R

import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelDialogFragment (private val cancelRun : () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {



        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel run")
            .setMessage("Are you sure you want to cancel this run?")
            .setIcon(R.drawable.ic_delete)
            .setNegativeButton("No"){ dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Yes") {_, _ ->
                cancelRun()
            }

            .create()
    }

}