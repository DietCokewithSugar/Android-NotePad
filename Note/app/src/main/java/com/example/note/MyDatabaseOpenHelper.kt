package com.example.note

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import org.jetbrains.anko.db.*
import java.util.*

class MyDatabaseOpenHelper private constructor(ctx: Context): ManagedSQLiteOpenHelper(ctx,"Note",null,1){

    init {
        instance = this
    }

    companion object{
        private var instance:MyDatabaseOpenHelper?=null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: MyDatabaseOpenHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("NoteBook",true,
            "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "title" to TEXT,
            "author" to TEXT,
            "content" to TEXT,
            "date" to TEXT,
            "pic" to BLOB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Notebook", true)
    }

}

