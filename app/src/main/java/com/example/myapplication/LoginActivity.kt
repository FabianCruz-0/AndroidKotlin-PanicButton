package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.net.NetworkInterface
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    var mac = getMac()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Iniciar Sesión"

        // Cambia color de la barra del título
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#280691"))
        actionBar?.setBackgroundDrawable(colorDrawable)

        auth = Firebase.auth

        db.collection("usuarios").document(mac).get().addOnSuccessListener { document ->
            if(document.get("Sesion").toString().equals("Open"))
            {
                val intent = Intent(this, MainActivity::class.java).apply{
                    putExtra("mac",mac)
                }
                this.startActivity(intent)
                finish()
            }
        }

        signInBtn.setOnClickListener {

            val mEmail = emailEditText.text.toString().trim()
            val mPassword = passwordEditText.text.toString().trim()
            when {
                mEmail.isEmpty() || mPassword.isEmpty() -> {
                    Toast.makeText(
                        baseContext, "Correo o contraseña incorrectos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    SignIn(mEmail, mPassword,mac)
                }
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        // Cambia color de la barra del título
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#280691"))
        actionBar?.setBackgroundDrawable(colorDrawable)

        super.onStart()
        val currentUser = auth.currentUser

        db.collection("usuarios").document(mac).get().addOnSuccessListener { document ->
            if(document.get("Sesion").toString().equals("Open"))
            {
                val intent = Intent(this, MainActivity::class.java).apply{
                    putExtra("mac",mac)
                }
                this.startActivity(intent)
                finish()
            }
        }

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java).apply{
                putExtra("mac",mac)
            }
            this.startActivity(intent)
            finish()
        }
    }

    private fun SignIn(email: String, password: String, mac: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "signInWithEmail:success")
                db.collection("usuarios").document(mac).update("Sesion","Open")
                val intent = Intent(this, MainActivity::class.java).apply{
                    putExtra("mac",mac)
                }
                this.startActivity(intent)
                finish()
            } else {
                Log.w("TAG", "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Correo o contraseña incorrectos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @JvmName("getMac1")
    private fun getMac(): String {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }
                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
        }
        return "02:00:00:00:00:00"
    }

}