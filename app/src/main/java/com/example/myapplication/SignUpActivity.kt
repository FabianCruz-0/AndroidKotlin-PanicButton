package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.NetworkInterface
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        signUpButton.setOnClickListener {

            val mEmail = emailEditText.text.toString()
            val mPassword = passwordEditText.text.toString()
            val name = nameEditText.text.toString()

            if (mEmail.isEmpty() || mPassword.isEmpty() || name.isEmpty()) {
                Toast.makeText(
                    baseContext, "Ingrese datos válidos.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                createAccount(mEmail, mPassword, name)
            }
        }
    }

    private fun createAccount(email: String, password: String, name: String) {

        var mac = getMac()

        db.collection("usuarios").document(mac).get().addOnSuccessListener {
            // YA HAY UNA CUENTA REGISTRADA CON ESTE TELÉFONO, SE TIENE QUE INICIAR SESIÓN
            Toast.makeText(
                baseContext, "Ya existe una cuenta en este teléfono, favor de iniciar sesión.",
                Toast.LENGTH_SHORT
            ).show()
        }
            .addOnFailureListener { exception ->
                // NO HAY NINGUNA CUENTA REGISTRADA A ESTE TELEFONO, SE PUEDE CREAR CUENTA
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            db.collection("usuarios").document(mac).set(
                                hashMapOf(
                                    "Id" to this.db.collection("usuarios").document().id,
                                    "Nombre" to name,
                                    "Email" to email,
                                    "Mac" to mac,
                                    "Contrasena" to password
                                )
                            )
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "La autenticación falló.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
    }

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