package com.example.demoapp2.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.demoapp2.R

class GridAdapter(val context: Context, val resource: Int, val gridArray: ArrayList<Int>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(resource, parent,false)

        //val name = gridArray["name"]
        val img = gridArray[position]

        Log.d("GridAdapter", "$gridArray")

        //view.findViewById<TextView>(R.id.tv_grid_name).text = name.toString()
         view.findViewById<ImageView>(R.id.img_grid).setImageResource(img)
        view.findViewById<TextView>(R.id.tv_grid_name).text = "Картинка ${position}"

        return view
    }

    //требуются для BaseAdapter тут свой массив ретурним
    override fun getItem(p0: Int): Any? {
        return gridArray[p0]
    }

    override fun getItemId(p0: Int): Long {
        return  p0.toLong()
    }

    override fun getCount(): Int {
        return gridArray.count()
    }
}