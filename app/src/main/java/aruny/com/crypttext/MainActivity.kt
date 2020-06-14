package aruny.com.crypttext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import com.scottyab.aescrypt.AESCrypt
import kotlinx.android.synthetic.main.activity_main.clearButton
import kotlinx.android.synthetic.main.activity_main.copyButton
import kotlinx.android.synthetic.main.activity_main.decryptButton
import kotlinx.android.synthetic.main.activity_main.encryptButton
import kotlinx.android.synthetic.main.activity_main.enteredText
import kotlinx.android.synthetic.main.activity_main.mainActivityRoot
import kotlinx.android.synthetic.main.activity_main.radio_pepper
import kotlinx.android.synthetic.main.activity_main.radio_tony
import kotlinx.android.synthetic.main.activity_main.resultText
import kotlinx.android.synthetic.main.activity_main.tgButton
import kotlinx.android.synthetic.main.activity_main.waButton
import java.security.GeneralSecurityException

const val WA_PACKAGE = "com.whatsapp"

const val WA_BASE = "https://wa.me/"

const val WA_BASE_SUFFIX = "@s.whatsapp.net"

const val TG_BASE = "https://t.me/"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPersonRadioButton()

        encryptButton.setOnClickListener {
            if (!TextUtils.isEmpty(enteredText.text.toString())) {
                val result = encrypt(enteredText.text.toString())
                setDecryptedContent(result)
                toggleSocialButtons(true)
                showSnackBar("Encryption done")
            } else{
                showSnackBar("Nothing entered to encrypt")
            }
        }

        decryptButton.setOnClickListener {
            if (!TextUtils.isEmpty(enteredText.text.toString())) {
                val result = decrypt(enteredText.text.toString())
                setDecryptedContent(result)
                showSnackBar("Decryption done")
            } else {
                showSnackBar("Nothing entered to decrypt")
            }
        }

        copyButton.setOnClickListener{
            copyResultToClipboard()
        }

        clearButton.setOnClickListener{
            clearAllEditTextFields()
        }

        waButton.setOnClickListener {
            handleWAClick()
        }

        tgButton.setOnClickListener {
            handleTGClick()
        }

    }

    private fun copyResultToClipboard() {
        if (!TextUtils.isEmpty(resultText.text.toString())) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", resultText.text.toString())
            clipboard.primaryClip = clip
            showSnackBar("Text copied to clip board")
        } else {
            showSnackBar("Nothing entered to copy")
        }
    }

    private fun setPersonRadioButton() {
        val person = getPersonPreference(personPreferenceKey)
        if (person == pepperPrefValue) {
            radio_pepper.isChecked = true
        } else {
            radio_tony.isChecked = true
        }
    }

    private fun setDecryptedContent(result: String?) {
        resultText.text = ""
        resultText.text = result
    }

    private fun handleWAClick() {
        copyResultToClipboard()
        val numberToUse = getWANumberToUse()

        val sendIntent = Intent(Intent.ACTION_MAIN)
        sendIntent.putExtra("jid", numberToUse + WA_BASE_SUFFIX)
        sendIntent.putExtra(Intent.EXTRA_TEXT, resultText.text.toString())
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.setPackage(WA_PACKAGE)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun getWANumberToUse(): String {
        val person = getPersonPreference(personPreferenceKey)
        return if (person == pepperPrefValue) {
            BuildConfig.ARUN_WA_NO
        } else {
            BuildConfig.SHAR_WA_NO
        }
    }

    private fun handleTGClick() {
        copyResultToClipboard()
        val tgId = getTgIDToUse()
        val tgUrl = StringBuilder()
        tgUrl.append(TG_BASE)
        tgUrl.append(tgId)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(tgUrl.toString())
        startActivity(intent)

    }

    private fun getTgIDToUse(): String {
        val person = getPersonPreference(personPreferenceKey)
        return if (person == pepperPrefValue) {
            BuildConfig.ARUN_TG_ID
        } else {
            BuildConfig.SHAR_TG_ID
        }
    }

    fun encrypt(message : String) : String? {
        var encryptedMessage = ""
        try {
            encryptedMessage = AESCrypt.encrypt(BuildConfig.CRYPT_PASSWORD, message)
        } catch (e: GeneralSecurityException) {
            return null
        }
        return encryptedMessage
    }

    fun decrypt(encryptedText: String) : String? {
        var decryptedMessage: String? = null
        try {
            decryptedMessage = AESCrypt.decrypt(BuildConfig.CRYPT_PASSWORD, encryptedText)
        } catch (e: GeneralSecurityException) {
            return decryptedMessage
        }
        return decryptedMessage
    }

    fun showSnackBar(message: String) {
        Snackbar.make(mainActivityRoot, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        clearAllEditTextFields()
        toggleSocialButtons(false)
    }

    private fun toggleSocialButtons(show: Boolean) {
        if (show) {
            waButton.visibility = View.VISIBLE
            tgButton.visibility = View.VISIBLE
        } else {
            waButton.visibility = View.INVISIBLE
            tgButton.visibility = View.INVISIBLE
        }
    }

    private fun clearAllEditTextFields() {
        enteredText.setText("")
        resultText.text = ""
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_pepper ->
                    if (checked) {
                        setPersonPreference(personPreferenceKey, pepperPrefValue)
                    }
                R.id.radio_tony ->
                    if (checked) {
                        setPersonPreference(personPreferenceKey, tonyPrefValue)
                    }
            }
        }
    }

    private fun setPersonPreference(prefKey: String, prefValue: Int) {
        val sharedPref = getSharedPreferences(
                personPreferenceFile, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt(prefKey, prefValue)
            commit()
        }
    }

    private fun getPersonPreference(prefKey: String) : Int {
        val sharedPref = getSharedPreferences(
                personPreferenceFile, Context.MODE_PRIVATE)
        return sharedPref.getInt(prefKey, pepperPrefValue)
    }
}

const val personPreferenceFile = "personPreferenceFile"

const val personPreferenceKey = "personPreferenceKey"

const val pepperPrefValue = 0

const val tonyPrefValue = 1

