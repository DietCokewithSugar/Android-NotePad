package com.example.note

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(activity : Activity, val resourceId : Int, data : List<NoteData>) : ArrayAdapter<NoteData>(activity,resourceId,data) {

    inner class ViewHolder(val noteTitle: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val viewHolder: ViewHolder
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,parent,false)
            val noteTitle : TextView = view.findViewById(R.id.noteTitle)
            viewHolder = ViewHolder(noteTitle)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val noteData = getItem(position)
        if (noteData != null){
            viewHolder.noteTitle.text = noteData.noteTitle
        }
        return view
    }


}