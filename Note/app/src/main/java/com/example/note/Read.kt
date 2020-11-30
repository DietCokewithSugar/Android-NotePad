package com.example.note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_read.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.update
import org.jetbrains.anko.startActivity

class Read : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        initNotes()
        writeNote()
    }

    private fun initNotes(){
        val dbHelper = MyDatabaseHelper(this, "Note.db",1)
        val db = dbHelper.writableDatabase
        val id = intent.getIntExtra("noteid",1).toString()
        var title = "1"
        var author = "Steven"
        var content = "Hello"
        var date = "20201010"
        val cursor = db.query("Note", null,"id=$id",null ,null,null,null)
        if(cursor.moveToNext()){
                 title = cursor.getString(cursor.getColumnIndex("title"))
                author = cursor.getString(cursor.getColumnIndex("author"))
                content = cursor.getString(cursor.getColumnIndex("content"))
                date = cursor.getString(cursor.getColumnIndex("date"))
        }
        cursor.close()
        authorText1.setText(author)
        titleText1.setText(title)
        contentText1.setText(content)
        timeView1.setText(date)
    }

    private fun writeNote(){
        val dbHelper = MyDatabaseHelper(this, "Note.db",1)
        val db = dbHelper.writableDatabase
        val id = intent.getIntExtra("noteid",1).toString()
        val title = titleText1.text
        val content = contentText1.text
        saveButton2.setOnClickListener() {
            db.update("Note","content" to "$content","title" to "$title").whereArgs("id = $id").exec()
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
            db.close()
            startActivity<MainActivity>()
        }
    }

    private fun deleteNote(){
        val dbHelper = MyDatabaseHelper(this, "Note.db",1)
        val db = dbHelper.writableDatabase
        val id = intent.getIntExtra("noteid",1).toString()
        db.use {
            db.execSQL("delete from Note where id = $id")
        }
        db.close()
    }

    /**
     * 注册Menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete,menu)
        return true
    }

    /**
     * Menu功能写入
     */
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        when(item.itemId){
            R.id.delete_item-> {
                deleteNote()
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show()
                startActivity<MainActivity>()
                finish()
            }
        }
        return true
    }

}