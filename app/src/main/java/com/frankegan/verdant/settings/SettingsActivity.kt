package com.frankegan.verdant.settings


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import com.frankegan.verdant.R
import org.jetbrains.anko.defaultSharedPreferences

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for
 * design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content,
                VerdantPreferenceFragment.newInstance()).commit()
    }

    class VerdantPreferenceFragment : PreferenceFragmentCompat() {

        companion object {
            fun newInstance(): VerdantPreferenceFragment {
                val args = Bundle()
                val fragment = VerdantPreferenceFragment()
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreatePreferences(bundle: Bundle, s: String) {
            addPreferencesFromResource(R.xml.prefs)
            //delete all data saved to shared preferences
            val clearData = findPreference("clear_data")
            clearData.setOnPreferenceClickListener { _ ->
                this@VerdantPreferenceFragment.activity?.defaultSharedPreferences?.edit()?.clear()?.apply()
                true
            }
        }

        override fun setDivider(divider: Drawable) {
            super.setDivider(ColorDrawable(Color.TRANSPARENT))
        }

        override fun setDividerHeight(height: Int) {
            super.setDividerHeight(0)
        }
    }
}
