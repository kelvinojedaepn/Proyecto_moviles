package com.example.crudfirebase

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AdapterListFood(
    var foodList: ArrayList<Food>,
    val idUser: String,
    val textTotalCalorias: TextView
) : RecyclerView.Adapter<AdapterListFood.MyViewHolderFood>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderFood {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return MyViewHolderFood(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderFood, position: Int) {
        val currentItem = foodList[position]
        holder.categoria.text = currentItem.category
        holder.peso.text = currentItem.quality.toString()
        holder.fecha.text = currentItem.date
        var reduceCalories: Double = 0.0
        holder.btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirmar eliminación de comida")
            builder.setMessage("Estás seguro que lo quieres eliminar está comida?")
            builder.setPositiveButton("Si") { dialog, _ ->
                dialog.dismiss()


                val totalCalorias =
                    this.textTotalCalorias.text.toString().toDouble() - currentItem.quality!!
                this.textTotalCalorias.text = totalCalorias.toString()



                deleteComida(currentItem.id!!, idUser)
                this.foodList.remove(currentItem)
                notifyDataSetChanged()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    fun getReduceCalories(idUser: String, id: String, callback: (Double) -> Unit) {
        val db = Firebase.firestore
        val comidasRef = db.collection("usuario").document(idUser).collection("comida")
        comidasRef.document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val reduceCalories = document.getDouble("quality") ?: 0.0
                    callback(reduceCalories)
                }
            } else {
                callback(0.0)
            }
        }
    }


    private fun deleteComida(id: String, idUser: String) {
        val db = Firebase.firestore
        val users = db.collection("usuario")
        val comidaCollectionsRef = users.document(idUser).collection("comida")
        comidaCollectionsRef.document(id).delete().addOnSuccessListener {

        }

    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateFoodList(foodListUpdate: ArrayList<Food>) {
        this.foodList = foodListUpdate
        notifyDataSetChanged()
    }


    class MyViewHolderFood(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoria = itemView.findViewById<TextView>(R.id.tv_categoria_recyclerview)
        val peso = itemView.findViewById<TextView>(R.id.tv_peso)
        val fecha = itemView.findViewById<TextView>(R.id.tv_date)
        val btnEliminar = itemView.findViewById<Button>(R.id.btn_eliminar_comida)
    }

}