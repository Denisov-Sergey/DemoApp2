package com.example.demoapp2.gallery_module

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.demoapp2.MainActivity
import com.example.demoapp2.R


class GalleryModuleAdapter(val context: Context, val resource: Int, val gridArray: MutableList<Bitmap>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(resource, parent,false)


        val bitmap: Bitmap = gridArray[position]

        Log.d(MainActivity.TAG, " Картинки в адаптере $gridArray")
        //view.findViewById<ImageView>(R.id.img_grid).setImageResource(img)
        view.findViewById<ImageView>(R.id.img_grid).setImageBitmap(bitmap)

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
