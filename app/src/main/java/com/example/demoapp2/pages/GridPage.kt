package com.example.demoapp2.pages

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.demoapp2.R
import com.example.demoapp2.data.GridAdapter
import java.text.FieldPosition


class GridPage : Fragment() {

    private var fragmentlayout: View? = null
    private var gridAdapter: GridAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentlayout = inflater.inflate(R.layout.fragment_grid_page, container, false)

        /**/
        val mThumbs = arrayListOf<Int>( R.drawable.gallery1,R.drawable.gallery2,R.drawable.gallery3,R.drawable.gallery4,R.drawable.gallery5)
        /**/
        val gridView = fragmentlayout!!.findViewById<GridView>(R.id.pic_grid)
        //val gridArray = mutableMapOf<Any, Any>("name" to "Pen")

        gridAdapter = activity?.applicationContext?.let { GridAdapter(it, R.layout.grid, mThumbs) }
        gridView.adapter = gridAdapter


        gridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, selected ->
                Toast.makeText(activity?.baseContext, "Картинка  $position", Toast.LENGTH_SHORT ).show()
                showImageDetail(mThumbs[position])
            }
        //gridAdapter!!.notifyDataSetChanged()


        return fragmentlayout
    }

        fun showImageDetail(imgId: Int){

            val  dialogBuilder = AlertDialog.Builder(getActivity())
            val layoutInflater: LayoutInflater = layoutInflater
            val dialogView: View = layoutInflater.inflate(R.layout.image_detail, null)
            //кнопки вслывашки
            val bigImage = dialogView.findViewById<ImageView>(R.id.detailImageView)
            bigImage.setImageResource(imgId)
            //

            dialogBuilder.setView(dialogView)
            //dialogBuilder.setTitle("Картинка $messageText")

            //
            val alertdialog: AlertDialog = dialogBuilder.create()

            //наполнение елементами
            bigImage.setOnClickListener{
                alertdialog.dismiss()
            }

            alertdialog.show()

        }
}