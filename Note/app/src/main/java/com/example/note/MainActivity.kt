package com.example.note

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(){

    var noteList = ArrayList<NoteData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNotes()
        val adapter = MyAdapter(this,R.layout.listview, noteList)
        listview.adapter = adapter
        listview.setOnItemClickListener{ _, _, position,_->
            val noteid = noteList[position].id
            val intent = Intent(this,Read::class.java)
            intent.putExtra("noteid",noteid)
            startActivity(intent)
        }
    }

    /**
     * 注册Menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    var dbHelper = MyDatabaseHelper(this, "Note.db",1)

    /**
     * Menu功能写入
     * 初始化数据库以及写日记
     */
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        when(item.itemId){
            R.id.write_item-> {
                startActivity<WriteActivity>()
                finish()
            }
            R.id.init_item-> {
                dbHelper.writableDatabase
                val editor = getSharedPreferences("data",Context.MODE_PRIVATE).edit()
                editor.putString("Author","Steven")
                editor.apply()
                Toast.makeText(this, "Succeed", Toast.LENGTH_SHORT).show() }
        }
        return true
    }

    /**
     *读取数据库内笔记并用ListView展示到MainActivity
     */
    private fun initNotes(){
        val db = dbHelper.writableDatabase
        val cursor = db.query("Note",null,null,null,null,null,"id DESC")
        if(cursor.moveToFirst()){
            do {
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                noteList.add(NoteData(title,id))
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }



}