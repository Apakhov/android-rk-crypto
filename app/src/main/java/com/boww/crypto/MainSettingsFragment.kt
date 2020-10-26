package com.boww.crypto


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.runBlocking

class MainSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_pref_fragment, rootKey)

        val editNumDaysVal = findPreference<EditTextPreference>(getString(R.string.num_days_key))
        editNumDaysVal?.setOnPreferenceChangeListener { _, newValue ->
            val newVal = newValue.toString()
            if (newVal == "") {
                return@setOnPreferenceChangeListener false
            }
            try {
                val v = newVal.toInt()
                if (v < 1 || v > 100) {
                    Toast.makeText(context, getString(R.string.set_num_days_bad_num), Toast.LENGTH_LONG).show()
                    return@setOnPreferenceChangeListener false
                }

                Toast.makeText(context, getString(R.string.set_num_days_ok_num), Toast.LENGTH_SHORT).show()
                true
            } catch (e: NumberFormatException) {
                Toast.makeText(context, getString(R.string.set_num_days_not_num), Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}