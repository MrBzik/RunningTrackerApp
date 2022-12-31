package com.example.runningtrackerapp.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentSetupBinding
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_IS_FIRST_RUN
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_NAME
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : BaseFragment<FragmentSetupBinding> (
    FragmentSetupBinding::inflate
        ) {

    @Inject
    lateinit var pref : SharedPreferences

    @set:Inject
    var isFirstRun = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstRun){

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState, navOptions
            )
        }


        bind.tvContinue.setOnClickListener {

            val success = savePreferences()

            if(success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)

            } else {

                Snackbar.make(requireView(),
                    "Put your name and weight first", Snackbar.LENGTH_SHORT).show()
            }
        }


    }


    private fun savePreferences() : Boolean{

        val name = bind.etName.text.toString()
        val weight = bind.etWeight.text.toString()

        if(name.isEmpty() || weight.isEmpty()){

            return false
        }

        pref.edit()
            .putString(SH_PREF_KEY_USER_NAME, name)
            .putFloat(SH_PREF_KEY_USER_WEIGHT, weight.toFloat())
            .putBoolean(SH_PREF_KEY_IS_FIRST_RUN, false)
            .apply()




        return true

    }



}