package com.easv.tkm.recipeapp.GUI

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easv.tkm.recipeapp.BuildConfig
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.RecyclerAdapter.RecyclerAdapterIngredient
import com.easv.tkm.recipeapp.data.IntentValues
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.IngredientEntry
import com.easv.tkm.recipeapp.data.Models.Recipe
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener
import kotlinx.android.synthetic.main.activity_crud.*
import kotlinx.android.synthetic.main.activity_crud.recyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CrudActivity : AppCompatActivity(), IClickItemListener<IngredientEntry> {

    val PERMISSION_REQUEST_CODE_CAMERA = 1
    var ingredients: MutableList<IngredientEntry> = mutableListOf()
    var categories: MutableList<Category> = mutableListOf()
    var mFile: File? = null
    private var recipeRepository = RecipeRepository.get()
    private var selectedCategory: Category? = null
    private lateinit var adapter: RecyclerAdapterIngredient

    private lateinit var recipe: Recipe

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                    val adapter = recyclerView.adapter as RecyclerAdapterIngredient
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ACTION_STATE_DRAG) { viewHolder?.itemView?.alpha = 0.5f }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder?.itemView?.alpha = 1.0f
                }

            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)
        ivImage.setOnClickListener { view -> checkCameraPermission()}
        btnAdd.setOnClickListener { view -> createIngredientEntry() }
        btnBack.setOnClickListener { view ->  setResult(Activity.RESULT_CANCELED, intent); finish()}
        btnCreate.setOnClickListener { view -> createRecipe() }
        btnUpdate.setOnClickListener { view -> updateRecipe() }
        btnDelete.setOnClickListener { view -> showDeleteDialog() }

        val ingredientListener = (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { validateIngredientEntry() }
        })

        val recipeListener = (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { validateRecipe() }
        })

        tvName.addTextChangedListener(ingredientListener)
        tvAmount.addTextChangedListener(ingredientListener)
        tvUnit.addTextChangedListener(ingredientListener)

        tvTitle.addTextChangedListener(recipeListener)
        tvDescription.addTextChangedListener(recipeListener)
        tvPreparations.addTextChangedListener(recipeListener)

        spCategories.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    selectedCategory = spCategories.selectedItem as Category
                    validateRecipe()
                } else if (position == 0) {
                    selectedCategory = null
                    validateRecipe()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapterIngredient(this, this, this.ingredients)
        recyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recipeRepository.getCategories().observe(this, Observer { categories ->
            this.categories = categories.toMutableList()
            this.categories.add(0, Category(0, "Select Category..."))
            spCategories.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                this.categories
            )
            if (intent.extras != null) {
                val categoryPosition =
                    this.categories.indexOfFirst { category -> category.id == recipe.categoryID }
                spCategories.setSelection(categoryPosition)
                this.selectedCategory = this.categories[categoryPosition]
            }
        })

        if(intent.extras != null){
            btnCreate.isVisible = false
            btnUpdate.isVisible = true
            btnDelete.isVisible = true

            recipe = intent.extras?.getSerializable("RECIPE") as Recipe
            ingredients = (intent.extras?.getSerializable("INGREDIENTS") as Array<IngredientEntry>).toMutableList()
            initializeText()
        }
    }

    fun initializeText(){
        tvTitle.setText(recipe.title)
        tvDescription.setText(recipe.description)
        tvPreparations.setText(recipe.preparations)

        this.mFile = File(recipe.imageURL)
        if (this.mFile!!.exists()) {
            ivImage.setImageURI(Uri.fromFile(mFile))
        }

        adapter.updateList(ingredients)
    }


    fun validateIngredientEntry(){
        btnAdd.isEnabled = tvName.text.isNotEmpty() && tvAmount.text.isNotEmpty() && tvUnit.text.isNotEmpty()
    }

    fun validateRecipe(){
        var isValid = tvTitle.text.isNotEmpty() && tvDescription.text.isNotEmpty() && tvPreparations.text.isNotEmpty() && this.ingredients.size > 0 && this.selectedCategory != null
        btnCreate.isEnabled = isValid
        btnUpdate.isEnabled = isValid
    }

    fun createRecipe(){
        val title = tvTitle.text.toString()
        val description = tvDescription.text.toString()
        val preparations = tvPreparations.text.toString()

        val recipe: Recipe = Recipe(
            0,
            this.selectedCategory!!.id,
            title,
            description,
            preparations,
            if (this.mFile != null && this.mFile!!.exists()) mFile!!.path else ""
        )

        val getDataJob = GlobalScope.async { recipeRepository.addRecipe(recipe, ingredients) }
        getDataJob.invokeOnCompletion { setResult(IntentValues.RESPONSE_DETAIL_CREATE.code, intent); finish()}
        Toast.makeText(this, "Recipe created", Toast.LENGTH_SHORT).show()
    }

    fun updateRecipe(){
        recipe.title = tvTitle.text.toString()
        recipe.description = tvDescription.text.toString()
        recipe.preparations = tvPreparations.text.toString()
        recipe.categoryID = this.selectedCategory!!.id
        recipe.imageURL = if (this.mFile != null && this.mFile!!.exists()) mFile!!.path else ""

        val getDataJob = GlobalScope.async { recipeRepository.updateRecipe(recipe, ingredients) }
        getDataJob.invokeOnCompletion { setResult(IntentValues.RESPONSE_DETAIL_UPDATE.code, intent); finish()}
        Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
    }

    fun showDeleteDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete entry")
        alertDialogBuilder
            .setMessage(Html.fromHtml("Delete recipe <b>${recipe.title}</b>?"))
            .setCancelable(true)
            .setPositiveButton("Delete") { dialog, id -> deleteRecipe() }
            .setNegativeButton("Cancel", { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun deleteRecipe(){
        val getDataJob = GlobalScope.async { recipeRepository.deleteRecipe(recipe) }
        getDataJob.invokeOnCompletion { setResult(IntentValues.RESPONSE_DETAIL_DELETE.code, intent); finish()}
        Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode === PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.all { permission -> permission == PackageManager.PERMISSION_GRANTED }) {
                showCameraDialog()
            }
        }
    }

    private fun checkCameraPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissions = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)
        if (permissions.size > 0)
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE_CAMERA
            )
        else
            showCameraDialog()
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val postfix = "jpg"
        val prefix = "IMG"
        return File(mediaStorageDir.path + File.separator + prefix + "_" + timeStamp + "." + postfix)
    }

    private fun showCameraDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Camera Handling")
        alertDialogBuilder
            .setMessage("Open build-in-camera-app or take picture directly?")
            .setCancelable(true)
            .setPositiveButton("Standard App") { dialog, id -> startCameraActivity() }
            .setNegativeButton("Directly", { dialog, id -> startInCameraActivity() })
            .setNeutralButton("File", { dialog, id -> pickImage() })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    fun pickImage(){
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        startActivityForResult(intent, IntentValues.REQUESTCODE_IMAGE_PICK.code)
    }

    private fun startCameraActivity() {
        mFile = getOutputMediaFile()
        if (mFile == null) {Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show(); return}

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val applicationId = BuildConfig.APPLICATION_ID
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                this,
                "${applicationId}.provider",
                mFile!!
            )
        )

        try{
            startActivityForResult(intent, IntentValues.REQUESTCODE_IMAGE_APP.code)
        }
        catch (e: ActivityNotFoundException){
            Toast.makeText(this, "Camera not found!", Toast.LENGTH_LONG).show()
        }
    }

    // Start direct camera
    private fun startInCameraActivity(){
        mFile = getOutputMediaFile()
        if (mFile == null) {Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show(); return}

        val intent = Intent(this, CameraX::class.java)
        intent.putExtra("FILEPATH", mFile)
        startActivityForResult(intent, IntentValues.REQUESTCODE_IMAGE_DIRECT.code)
    }

    private fun createIngredientEntry(){

        val title: String = tvName.text.toString()
        val amount: Double = tvAmount.text.toString().toDouble()
        val measureUnit: String = tvUnit.text.toString()

        tvName.setText("")
        tvAmount.setText("")
        tvUnit.setText("")

        val ingredientEntry = IngredientEntry(0, 0, title, amount, measureUnit)
        this.ingredients.add(ingredientEntry)
        this.adapter.updateList()
        this.validateRecipe()
    }

    override fun onItemClick(ingredient: IngredientEntry) {
        this.ingredients.remove(ingredient)
        this.adapter.updateList()
        this.validateRecipe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IntentValues.REQUESTCODE_IMAGE_APP.code -> if (resultCode == RESULT_OK) {
                ivImage.setImageURI(
                    Uri.fromFile(
                        mFile
                    )
                )
            }
            IntentValues.REQUESTCODE_IMAGE_DIRECT.code -> if (resultCode == RESULT_OK) {
                ivImage.setImageURI(
                    Uri.fromFile(
                        mFile
                    )
                )
            }
            IntentValues.REQUESTCODE_IMAGE_PICK.code -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri = data!!.data!!
                ivImage.setImageURI(selectedImage)
                mFile = File(getPathFromUri(selectedImage))
            }
            else -> false
        }
    }

    fun getPathFromUri(uri: Uri): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally { cursor?.close() }
        return null
    }
}