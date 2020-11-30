package com.example.note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.activity_write.fromAlbumButton
import kotlinx.android.synthetic.main.activity_write.imageView
import kotlinx.android.synthetic.main.activity_write.takePhotoButton
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Blob
import java.text.SimpleDateFormat


class WriteActivity : AppCompatActivity() {

    var res: ByteArray = ByteArray(100000)
    val takePhoto = 1
    val fromAlbum = 2
    lateinit var imageUri: Uri
    lateinit var outputImage : File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        writeNote()

        takePhotoButton.setOnClickListener {
            // 创建File对象，用于存储拍照后的图片
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "com.example.note.fileprovider", outputImage);
            } else {
                Uri.fromFile(outputImage);
            }
            // 启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }

        fromAlbumButton.setOnClickListener {
            // 打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示照片
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)
        }

    }

    private fun writeNote(){
        var pic = 101010
        val prefs = getSharedPreferences("data",Context.MODE_PRIVATE)
        val authorname = prefs.getString("Author","") //使用SharePreference自动初始化作者
        authorText.setText(authorname)
        var id = 0
        val sysTime = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
        var date = System.currentTimeMillis()
        val datatime = sysTime.format(date)
        val title = titleText.text
        val author = authorText.text
        val content = contentText.text
//        val pic = 101010
        val dbHelper = MyDatabaseHelper(this, "Note.db",1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Note",null,null,null,null,null,"ID DESC")
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex("id"))
        }
        cursor.close()

        /**
         * 保存按钮
         */
        saveButton.setOnClickListener {
            id += 1
            db.execSQL("insert into Note values('$id','$title','$author','$content','$res','$datatime')")
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
            db.close()
            startActivity<MainActivity>()
        }

        timeView.setText(datatime)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    res = BitmapUtils.bmpToByteArray(bitmap,false)
                    imageView.setImageBitmap(rotateIfRequired(bitmap))
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        val bitmap = getBitmapFromUri(uri)
                        res = BitmapUtils.bmpToByteArray(bitmap,false)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    object BitmapUtils {
        private val TAG = "BitmapUtils"

        fun bmpToByteArray(bmp: Bitmap?, needRecycle: Boolean): ByteArray {
            val output = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.PNG, 100, output)//设置图片格式压缩相关
            if (needRecycle) {
                    bmp.recycle()
            }

            val result = output.toByteArray()
            try {
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result//输出
        }
    }

}