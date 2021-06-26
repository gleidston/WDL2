package com.example.colormyview3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    val tag = "COLORS"
    var color: Int = R.color.gray
    var boxes = arrayOf(R.id.boxOne, R.id.boxTwo,R.id.boxThree,R.id.boxFour,R.id.boxFive)
    lateinit var shareButton: FloatingActionButton
    lateinit var view: View
    lateinit var  prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shareButton = findViewById(R.id.shareButton)
        view = findViewById(R.id.parentLayout)

        prefs = getSharedPreferences("colors", MODE_PRIVATE)
        for (box in boxes) {
            findViewById<View>(box).setBackgroundResource(prefs.getInt("box-$box", R.color.gray))
        }
        shareButton.setOnClickListener{
            val bitmap = getViewAsBitmap(view)
            if (bitmap != null) {
                saveScreenshot(bitmap, "ScreenshotView")
            }
        }
    }
    fun onButtonClick(view: View){
        this.color = when(view.id){
            R.id.buttonRed -> R.color.red
            R.id.buttonYellow -> R.color.yellow
            else -> R.color.green
        }
    }
    fun onBoxClick(view: View){
        view.setBackgroundResource(this.color)
        var id = view.id
        with(prefs.edit()){
            putInt("box-$id", color)
            commit()
        }
    }

    // To share Image --------------
    private fun getViewAsBitmap(mView: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(mView.width, mView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDraw = mView.background
        if (bgDraw != null)
            bgDraw.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        mView.draw(canvas)
        return bitmap
    }
    private fun saveScreenshot(imageBitmap: Bitmap, filename: String) {
        val dirPath = applicationContext.filesDir
        val file = File(dirPath, "$filename.jpg")
        val fileOutputStream = FileOutputStream(file)
        try {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
            fileOutputStream.apply {
                flush()
                close()
            }
            val imageUri = FileProvider.getUriForFile(
                this@MainActivity,
                "com.devventure.colormyviews.provider",
                file
            )
            shareImageUri(imageUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun shareImageUri(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
        }
        applicationContext?.packageManager?.run {
            if (shareIntent.resolveActivity(this) != null)
                startActivity(Intent.createChooser(shareIntent, "Share images to.."))
            else
                Toast.makeText(applicationContext, "Impossível executar", Toast.LENGTH_LONG).show()
        }
    }
}