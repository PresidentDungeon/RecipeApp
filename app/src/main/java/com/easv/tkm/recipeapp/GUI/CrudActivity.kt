package com.easv.tkm.recipeapp.GUI

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.easv.tkm.recipeapp.BuildConfig
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.RecyclerAdapter.RecyclerAdapter
import com.easv.tkm.recipeapp.RecyclerAdapter.RecyclerAdapterIngredient
import com.easv.tkm.recipeapp.data.IntentValues
import com.easv.tkm.recipeapp.data.Models.IngredientEntry
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener
import kotlinx.android.synthetic.main.activity_crud.*
import kotlinx.android.synthetic.main.activity_crud.recyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrudActivity : AppCompatActivity(), IClickItemListener<IngredientEntry> {

    val PERMISSION_REQUEST_CODE_CAMERA = 1
    val ingredients: MutableList<IngredientEntry> = mutableListOf()
    var ingredientEntryID: Int = 0
    var mFile: File? = null
    private lateinit var adapter: RecyclerAdapterIngredient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)
        ivImage.setOnClickListener { view -> checkCameraPermission()}
        btnAdd.setOnClickListener { view -> createIngredientEntry() }


        val listener = (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { validateIngredientEntry() }
        })

        tvName.addTextChangedListener(listener)
        tvAmount.addTextChangedListener(listener)
        tvUnit.addTextChangedListener(listener)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapterIngredient(this, this, this.ingredients)
        recyclerView.adapter = adapter
    }


    fun validateIngredientEntry(){
        btnAdd.isEnabled = tvName.text.isNotEmpty() && tvAmount.text.isNotEmpty() && tvUnit.text.isNotEmpty()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode === PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.all { permission -> permission == PackageManager.PERMISSION_GRANTED }) {
                showCameraDialog()
            }
        }

        this
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissions = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)
        if (permissions.size > 0)
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE_CAMERA)
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
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun startCameraActivity() {
        mFile = getOutputMediaFile()
        if (mFile == null) {Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show(); return}

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val applicationId = BuildConfig.APPLICATION_ID
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "${applicationId}.provider", mFile!!))

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

        val ingredientEntry = IngredientEntry(0, title, amount, measureUnit)
        this.ingredients.add(ingredientEntry)
        this.adapter.updateList()
    }

    override fun onItemClick(ingredient: IngredientEntry) {
        this.ingredients.remove(ingredient)
        this.adapter.updateList()
    }


}