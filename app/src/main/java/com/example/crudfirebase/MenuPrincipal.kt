package com.example.crudfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MenuPrincipal : AppCompatActivity() {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var adaptador: AdapterListFood
    private lateinit var textTotalCalorias: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_prinicipal)
        val id = intent.getStringExtra("usuarioId")

        foodRecyclerView = this.findViewById<RecyclerView>(R.id.foodRcyclerView)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        foodRecyclerView.setHasFixedSize(true)
        textTotalCalorias = this.findViewById(R.id.tv_total_caloria)
        foodArrayList = arrayListOf<Food>()
        adaptador = AdapterListFood(foodArrayList, id!!, textTotalCalorias)
        foodRecyclerView.adapter = adaptador


        val checkCarbo = this.findViewById<CheckBox>(R.id.checkBoxCarbo)
        val checkProteina = this.findViewById<CheckBox>(R.id.checkBoxProteina)
        val checkGrasa = this.findViewById<CheckBox>(R.id.checkBoxGrasa)

        val btnBuscar = this.findViewById<Button>(R.id.btn_bucar_calorias_consumidas)
        val btnLogOut = this.findViewById<Button>(R.id.btn_log_out)


        val selectedCategories = mutableListOf<String>()
        checkCarbo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCategories.add("Carbohidrato")
            } else {
                selectedCategories.remove("Carbohidrato")
            }
        }
        checkProteina.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCategories.add("Proteína")
            } else {
                selectedCategories.remove("Proteína")
            }
        }
        checkGrasa.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCategories.add("Grasa")
            } else {
                selectedCategories.remove("Grasa")
            }
        }
        consultarComida(selectedCategories, id!!)
        btnBuscar.setOnClickListener {
            consultarComida(selectedCategories, id!!)
        }





        val btnAddFood = this.findViewById<Button>(R.id.btn_add_caloria)
        btnAddFood.setOnClickListener {
            abrirActividadConParametros(AddFoodActivity::class.java, it, id!!)
        }

        btnLogOut.setOnClickListener {
            irActividad(LoginActivity::class.java)
        }


    }

    private fun consultarComida(selectedCategories: MutableList<String>, id: String) {
        var totalCalorias = 0.0
        val db = Firebase.firestore
        val usuarioRef = db.collection("usuario").document(id)
        val comidaCollectionRef = usuarioRef.collection("comida")
        limpiarArreglo()
        if(selectedCategories.size == 0){

            comidaCollectionRef.get().addOnSuccessListener { querySnaps ->
                for (document in querySnaps.documents) {
                    val food = Food(
                        document.id,
                        document.get("category") as String?,
                        (document.get("quality") as Number?)?.toDouble(),
                        document.get("date") as String
                    )
                    totalCalorias += food!!.quality!!
                    this.foodArrayList.add(food)

                }
                this.textTotalCalorias.text = totalCalorias.toString()
                adaptador.foodList = foodArrayList
                adaptador.notifyDataSetChanged()
            }
        }else{
            val query = comidaCollectionRef.whereIn("category", selectedCategories)
            query.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val food = Food(
                        document.id,
                        document.get("category") as String?,
                        (document.get("quality") as Number?)?.toDouble(),
                        document.get("date") as String
                    )
                    totalCalorias += food!!.quality!!
                    this.foodArrayList.add(food)

                }
                this.textTotalCalorias.text = totalCalorias.toString()
                adaptador.foodList = foodArrayList
                adaptador.notifyDataSetChanged()
            }
        }

    }

    private fun limpiarArreglo() {
        this.foodArrayList.clear()
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