package com.example.crudfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin = this.findViewById<Button>(R.id.btnLogin)
        val textEmail = this.findViewById<TextInputLayout>(R.id.tilEmail)
        val textPassword = this.findViewById<TextInputLayout>(R.id.tilPassword)
        val btnSingIn = this.findViewById<Button>(R.id.btnSignUp)

        btnSingIn.setOnClickListener {
            irActividad(SignInActivity::class.java)
        }

        var usuario: Usuario? = null
        btnLogin.setOnClickListener {
            consultarUsuario(textEmail.editText?.text.toString(), textPassword.editText?.text.toString()) { usuarioLogueado ->
                if(usuarioLogueado != null){
                    abrirActividadConParametros(MenuPrincipal::class.java, it, usuarioLogueado.id!!)
                }else{
                    Toast.makeText(this, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun consultarUsuario(
        textEmail: String,
        textPassword: String,
        callback: (Usuario?) -> Unit
    ) {
        val db = Firebase.firestore
        val usuarioRef = db.collection("usuario")
        usuarioRef.get().addOnSuccessListener { result ->
            var usuarioLogueado: Usuario? = null
            for (document in result) {
                val usuario = Usuario(
                    document.get("id") as String?,
                    document.get("email") as String?,
                    document.get("password") as String?
                )
                if (usuario.email == textEmail && usuario.password == textPassword) {
                    usuarioLogueado = usuario
                    break
                }
            }
            callback(usuarioLogueado)
        }.addOnFailureListener { exception ->
            Log.d(null, "Error al obtener casas", exception)
            callback(null)
        }
    }


    private val contenidoIntentExplicito = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {
                val data = result.data
                Log.i("Intente-epn", "${data?.getStringExtra("nombreModificado")}")
            }
        }
    }

    private fun abrirActividadConParametros(clase: Class<*>, it: View?, usuarioId: String) {
        val intent = Intent(it!!.context, clase)
        intent.putExtra("usuarioId", usuarioId)
        contenidoIntentExplicito.launch(intent)
    }
    fun irActividad(
        clase: Class<*>
    ) {
        val intent = Intent(this, clase)
        startActivity(intent)
    }


}