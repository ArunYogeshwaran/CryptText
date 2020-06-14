package aruny.com.crypttext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.scottyab.aescrypt.AESCrypt
import kotlinx.android.synthetic.main.activity_main.clearButton
import kotlinx.android.synthetic.main.activity_main.copyButton
import kotlinx.android.synthetic.main.activity_main.decryptButton
import kotlinx.android.synthetic.main.activity_main.decryptedText
import kotlinx.android.synthetic.main.activity_main.encryptButton
import kotlinx.android.synthetic.main.activity_main.enteredText
import kotlinx.android.synthetic.main.activity_main.mainActivityRoot
import java.security.GeneralSecurityException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        encryptButton.setOnClickListener {
            if (!TextUtils.isEmpty(enteredText.text.toString())) {
                val result = encrypt(enteredText.text.toString())
                decryptedText.setText("")
                decryptedText.setText(result)
                decryptedText.selectAll()
                showSnackBar("Encryption done")
            } else{
                showSnackBar("Nothing entered to encrypt")
            }
        }
        decryptButton.setOnClickListener {
            if (!TextUtils.isEmpty(enteredText.text.toString())) {
                val result = decrypt(enteredText.text.toString())
                decryptedText.setText("")
                decryptedText.setText(result)
                decryptedText.selectAll()
                showSnackBar("Decryption done")
            } else {
                showSnackBar("Nothing entered to decrypt")
            }
        }
        copyButton.setOnClickListener{
            if (!TextUtils.isEmpty(decryptedText.text.toString())) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", decryptedText.text.toString())
                clipboard.primaryClip = clip
                showSnackBar("Text copied to clip board")
            } else {
                showSnackBar("Nothing entered to copy")
            }
        }
        clearButton.setOnClickListener{
            enteredText.setText("")
            decryptedText.setText("")
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
}
