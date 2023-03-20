package com.example.crudfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddFoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        var id = intent.getStringExtra("usuarioId")
        var categoria = ""

        val optionCategoria = this.findViewById<RadioGroup>(R.id.rg_categoria)
        val optionCarbohidrato = this.findViewById<RadioButton>(R.id.option_carbohidrato)
        val optionGrasa = this.findViewById<RadioButton>(R.id.option_grasa)
        val optionProteina = this.findViewById<RadioButton>(R.id.option_proteina)
        val btnAddFood = this.findViewById<Button>(R.id.btn_add_food)
        val textQuantities = this.findViewById<EditText>(R.id.tit_total_caloría)

        optionCategoria.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                optionGrasa.id->{
                    categoria = "Grasa"
                }
                optionCarbohidrato.id->{
                    categoria = "Carbohidrato"
                }
                optionProteina.id->{
                    categoria = "Proteína"
                }
            }
        }

        btnAddFood.setOnClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = sdf.format(Date())
            saveData(id!!, categoria, currentDate, textQuantities.text.toString().toDouble())
            abrirActividadConParametros(MenuPrincipal::class.java, it, id!!)
        }



    }

    private fun saveData(idUser: String, categoria: String, currentDate: String, textQuantities: Double) {
        val db = Firebase.firestore
        val usuario = db.collection("usuario")
        val usuarioCollectionsRef = usuario.document(idUser!!).collection("comida")
        val dataFood = hashMapOf(
            "category" to categoria,
            "quality" to textQuantities,
            "date" to currentDate
        )
        usuarioCollectionsRef.add(dataFood)

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

}