package com.easv.tkm.recipeapp.RecyclerAdapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.Recipe
import com.easv.tkm.recipeapp.data.Models.RecipeWithIngredients
import com.easv.tkm.recipeapp.data.Sorting
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener
import com.easv.tkm.recipeapp.data.interfaces.IMenuUpdate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File


class RecyclerAdapter: RecyclerView.Adapter<RecyclerHolder>{

    private var mInflater: LayoutInflater
    private var recipeRepository: RecipeRepository = RecipeRepository.get()
    private var itemListener: IClickItemListener<RecipeWithIngredients>
    private var menuUpdate: IMenuUpdate
    private var recipeList: List<RecipeWithIngredients> = emptyList()
    private var sortingType: Sorting = Sorting.SORTING_NAME
    private var context: Context

    constructor(context: Context, itemClickListener: IClickItemListener<RecipeWithIngredients>, menuUpdate: IMenuUpdate) : super(){
        this.mInflater = LayoutInflater.from(context)
        this.itemListener = itemClickListener
        this.menuUpdate = menuUpdate
        this.context = context

        val getDataJob = GlobalScope.async { recipeRepository.getRecipes("SELECT * FROM Recipe ORDER BY title COLLATE NOCASE ASC", emptyArray()) }
        getDataJob.invokeOnCompletion { _ -> val myData = getDataJob.getCompleted(); this.recipeList = myData; (context as AppCompatActivity).runOnUiThread { notifyDataSetChanged()}}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val view: View = mInflater.inflate(R.layout.cell_recipe, parent, false)
        return RecyclerHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val recipe = recipeList[position]
        holder.view.setOnClickListener { view -> itemListener.onItemClick(recipe) }
        holder.bind(recipe.recipe)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun filter(text: String, category: Category?) {

        var queryString = "SELECT * FROM Recipe"
        var containsCondition = false
        val args = mutableListOf<Any>()

        if(text.isNotEmpty()){
            queryString += " WHERE"
            queryString += " title LIKE '%' || ? || '%'"
            args.add(text)
            containsCondition = true
        }

        if(category != null){

            if(containsCondition){
                queryString += " AND"
            }else{
                queryString += " WHERE"
                containsCondition = true
            }

            queryString += " categoryID = ?"
            args.add(category.id)
        }

        queryString += sortingType.query
        Log.d("XXXX", queryString)

        val getDataJob = GlobalScope.async {recipeRepository.getRecipes(queryString, args.toTypedArray()) }
        getDataJob.invokeOnCompletion { _ -> val myData = getDataJob.getCompleted(); this.recipeList = myData
            (context as AppCompatActivity).runOnUiThread { notifyDataSetChanged()} }
    }

    fun setSortingType(sortingType: Sorting){

        if(this.sortingType == sortingType){
            when(sortingType){
                Sorting.SORTING_NAME -> { this.sortingType = Sorting.SORTING_NAME_DESC;}
                Sorting.SORTING_AGE -> { this.sortingType = Sorting.SORTING_AGE_DESC;}
            }
        }

        else this.sortingType = sortingType
        menuUpdate.updateMenu(this.sortingType)
    }
}


class RecyclerHolder(view: View) : RecyclerView.ViewHolder(view) {
    lateinit var recipe: Recipe
    val view: View = view
    val titleText: TextView = view.findViewById(R.id.tvFoodName)
    val ivFood: ImageView = view.findViewById(R.id.ivFood)

    fun bind(recipe: Recipe){
        this.recipe = recipe;
        this.titleText.text = recipe.title

        var file: File = File(this.recipe.imageURL)
        if (file!!.exists()){this.ivFood.setImageURI(Uri.fromFile(file))}
        else{this.ivFood.setImageResource(R.drawable.placeholder)}
    }

    init { }
}