package com.easv.tkm.recipeapp.RecyclerAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.data.Models.IngredientEntry
import com.easv.tkm.recipeapp.data.Sorting
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener

class RecyclerAdapterIngredient: RecyclerView.Adapter<RecyclerHolderIngredient>{

    private var mInflater: LayoutInflater
    private var itemListener: IClickItemListener<IngredientEntry>
    private var ingredientList: MutableList<IngredientEntry> = mutableListOf()
    private var context: Context

    constructor(context: Context, itemClickListener: IClickItemListener<IngredientEntry>, ingredients: MutableList<IngredientEntry>) : super(){
        this.mInflater = LayoutInflater.from(context)
        this.itemListener = itemClickListener
        this.context = context
        this.ingredientList = ingredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolderIngredient {
        val view: View = mInflater.inflate(R.layout.cell_ingredient, parent, false)
        return RecyclerHolderIngredient(view)
    }

    override fun onBindViewHolder(holder: RecyclerHolderIngredient, position: Int) {
        val ingredient = ingredientList[position]
        holder.bind(ingredient, this.itemListener)
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun updateList(){
        notifyDataSetChanged()
    }

    fun updateList(ingredients: MutableList<IngredientEntry>){
        this.ingredientList = ingredients
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int){
        var temp: IngredientEntry = ingredientList[to]
        ingredientList[to] = ingredientList[from]
        ingredientList[from] = temp
    }

}


class RecyclerHolderIngredient(view: View) : RecyclerView.ViewHolder(view) {
    lateinit var ingredient: IngredientEntry
    val view: View = view
    val tvName: TextView = view.findViewById(R.id.tvName)
    val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    val tvMeasure: TextView = view.findViewById(R.id.tvMeasure)
    val btnRemove: Button = view.findViewById(R.id.btnRemove)

    fun bind(ingredient: IngredientEntry, IClickItemListener: IClickItemListener<IngredientEntry>){
        this.ingredient = ingredient
        this.tvName.text = ingredient.name
        this.tvAmount.text = ingredient.amount.toString()
        this.tvMeasure.text = ingredient.measurementUnit
        btnRemove.setOnClickListener { IClickItemListener.onItemClick(this.ingredient); }
    }

    init { }
}