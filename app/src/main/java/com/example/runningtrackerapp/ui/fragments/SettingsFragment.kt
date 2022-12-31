package com.example.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.runningtrackerapp.databinding.FragmentSettingsBinding
import com.example.runningtrackerapp.ui.MainActivity
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_NAME
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(
        FragmentSettingsBinding::inflate
) {

 @Inject
 lateinit var pref : SharedPreferences

 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

                loadPrefValues()

         bind.btnApplyChanges.setOnClickListener{

        val name = bind.etName.text.toString()
        val weight = bind.etWeight.text.toString()

        if(name.isNotEmpty()) {
                pref.edit().putString(SH_PREF_KEY_USER_NAME, name).apply()
                (activity as MainActivity).toolbarText.text = "Let's go, $name!"
        }
         if(weight.isNotEmpty()) {
                 pref.edit().putFloat(SH_PREF_KEY_USER_WEIGHT, weight.toFloat()).apply()
         }

         }


 }

        private fun loadPrefValues(){

                val name = pref.getString(SH_PREF_KEY_USER_NAME, "noname")
                val weight = pref.getFloat(SH_PREF_KEY_USER_WEIGHT, 55f)

                bind.etName.setText(name)
                bind.etWeight.setText(weight.toString())

        }


}