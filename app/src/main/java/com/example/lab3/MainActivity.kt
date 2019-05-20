package com.example.lab3

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val REQUEST_TAKE_PHOTO = 0
    private var imageURI: Uri? = null
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName: String = "JPEG_" + timeStamp
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonMakePhoto.setOnClickListener { v ->
            if (editTextName.text.isEmpty()) {
                Snackbar.make(v, getString(R.string.text_empty_editText), Snackbar.LENGTH_LONG).show()
            }
            else{
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    try {
                        imageURI = FileProvider.getUriForFile(
                            applicationContext,
                            "com.example.android.fileprovider",
                            createImageFile() )
                        it.putExtra(MediaStore.EXTRA_OUTPUT,
                            imageURI)

                        startActivityForResult(it, REQUEST_TAKE_PHOTO);
                    } catch (ex: IOException) {
                        Snackbar.make(v, getString(R.string.error_creation_file), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val intent = Intent(applicationContext, ImageActivity::class.java)
            intent.data =  imageURI
            intent.putExtra("name", editTextName.text.toString() )
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null)
            outState.putString("imageURI", imageURI.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null)
            imageURI = Uri.parse(savedInstanceState.getString("imageURI"))
    }
}
