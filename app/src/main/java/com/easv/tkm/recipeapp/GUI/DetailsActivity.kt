package com.easv.tkm.recipeapp.GUI

import android.app.Activity
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import com.easv.tkm.recipeapp.BuildConfig
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.data.IntentValues
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.IngredientEntry
import com.easv.tkm.recipeapp.data.Models.Recipe
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_details.tvDescription
import kotlinx.android.synthetic.main.activity_details.tvPreparations
import kotlinx.android.synthetic.main.activity_details.tvTitle
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

class DetailsActivity : AppCompatActivity() {

    private var recipeRepository = RecipeRepository.get()
    private lateinit var recipe: Recipe
    private lateinit var ingredients: Array<IngredientEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        this.recipe = intent.extras?.getSerializable("RECIPE") as Recipe
        this.ingredients = intent.extras?.getSerializable("INGREDIENTS") as Array<IngredientEntry>

        initializeText()

    }

    fun initializeText() {
        tvTitle.setText(recipe.title)
        tvDescription.setText(recipe.description)
        tvPreparations.setText(recipe.preparations)

        var file: File = File(this.recipe.imageURL)
        if (file!!.exists()){ivImage.setImageURI(Uri.fromFile(file))}
        else{ivImage.setImageResource(R.drawable.placeholder)}
        updateIngredients()
    }

   /* private fun updateIngredients() {
        var ingredientString = ""

        ingredients.forEach{ ingredient -> ingredientString += "${ingredient.name}: ${ingredient.amount} ${ingredient.measurementUnit} \n" }
        tvIngredients.setText(ingredientString)
    }*/

    private fun updateIngredients() {
        var nameString = ""
        var amountString = ""
        var unitString = ""

        ingredients.forEach{ ingredient -> nameString += "${ingredient.name}: \n" }
        ingredients.forEach{ ingredient -> amountString += "${ingredient.amount} \n" }
        ingredients.forEach{ ingredient -> unitString += "${ingredient.measurementUnit} \n" }
        inName.setText(nameString)
        inAmount.setText(amountString)
        inUnit.setText(unitString)
    }


    fun share(view: View) {

        val sendIntent = Intent(Intent.ACTION_SEND,Uri.parse("sms:"))

        if(File(recipe.imageURL).exists())
        {
            val path = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", File(recipe.imageURL))
            sendIntent.putExtra(Intent.EXTRA_STREAM, path)
        }

        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.type = "image/png"
        sendIntent.putExtra("sms_body", preparedSMS())
        startActivity(sendIntent)
    }

    private fun preparedSMS(): String {
        var SMS = ""
        var ingredientString = ""

        ingredients.forEach{ ingredient -> ingredientString += "${ingredient.name}: ${ingredient.amount} ${ingredient.measurementUnit} \n" }

        SMS += "${recipe.title} \n${recipe.description} \n\nIngredients:\n${ingredientString} \nDirections:\n${recipe.preparations}"

        return SMS
    }

    fun openEdit(view: View){
        val intent = Intent(this, CrudActivity::class.java)
        intent.putExtra("RECIPE", recipe)
        intent.putExtra("INGREDIENTS", ingredients)
        startActivityForResult(intent, IntentValues.REQUESTCODE_CRUD.code)
    }

    fun goBack(view: View){
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IntentValues.REQUESTCODE_CRUD.code && resultCode == IntentValues.RESPONSE_DETAIL_UPDATE.code) {
            val getDataJob = GlobalScope.async { recipeRepository.getRecipeByID(recipe.id) }
            getDataJob.invokeOnCompletion { _ -> val recipeWithIngredients = getDataJob.getCompleted(); this.recipe = recipeWithIngredients.recipe; this.ingredients = recipeWithIngredients.ingredientEntries.toTypedArray(); runOnUiThread { initializeText()}}
            setResult(IntentValues.RESPONSE_DETAIL_UPDATE.code, intent)
        }
        else if(requestCode == IntentValues.REQUESTCODE_CRUD.code && resultCode == IntentValues.RESPONSE_DETAIL_DELETE.code){
            setResult(IntentValues.RESPONSE_DETAIL_DELETE.code, intent)
            finish()
        }
    }

}