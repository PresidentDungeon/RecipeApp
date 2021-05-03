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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.easv.tkm.recipeapp.BuildConfig
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.data.IntentValues
import kotlinx.android.synthetic.main.activity_crud.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrudActivity : AppCompatActivity() {

    val PERMISSION_REQUEST_CODE_CAMERA = 1
    var mFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        ivImage.setOnClickListener { view -> checkCameraPermission()}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode === PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.all { permission -> permission == PackageManager.PERMISSION_GRANTED }) {
                showCameraDialog()
            }
        }
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
}