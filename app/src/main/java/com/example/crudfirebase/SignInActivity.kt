package com.example.crudfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val btnLogin = this.findViewById<Button>(R.id.btnLogin_sign)
        val textEmail = this.findViewById<TextInputLayout>(R.id.tilEmail_sign)
        val textPassword = this.findViewById<TextInputLayout>(R.id.tilPassword_sign)
        val btnSingIn = this.findViewById<Button>(R.id.btnSignUp_sign)
        btnSingIn.setOnClickListener {
            if(textEmail.editText?.text.toString() != "" && textPassword.editText?.text.toString() != ""){
                saveData(textEmail.editText?.text.toString(), textPassword.editText?.text.toString())
                irActividad(LoginActivity::class.java)
            }else{
                Toast.makeText(this, "Ingrese un email y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
        btnLogin.setOnClickListener {
            irActividad(LoginActivity::class.java)
        }


    }

    private fun saveData(textEmail: String, textPassword: String) {
        val db = Firebase.firestore
        val usuario = db.collection("usuario")
        val id = Date().time.toString()
        val dataUser = hashMapOf(
            "id" to id,
            "email" to textEmail,
            "password" to textPassword
        )
        usuario.document(id).set(dataUser)





    }

    fun irActividad(
        clase: Class<*>
    ) {
        val intent = Intent(this, clase)
        startActivity(intent)
    }

}