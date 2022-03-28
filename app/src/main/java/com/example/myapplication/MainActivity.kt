package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Menú principal"

        // Cambia color de la barra del título
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#280691"))
        actionBar?.setBackgroundDrawable(colorDrawable)

        var usr = intent.getStringExtra("mac").toString()

        db.collection("usuarios").document(usr).get().addOnSuccessListener { document ->
            headerTextView.text = "Bienvenido " + document.get("Nombre").toString()
        }
    }
}