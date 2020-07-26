package com.example.demoapp2.pages

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demoapp2.R
import kotlinx.android.synthetic.main.fragment_sorter_page.*
import kotlinx.android.synthetic.main.fragment_sorter_page.view.*
import java.util.*

class SorterPage : Fragment() {


    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ссылка на макет фрагмента
        val fragmentlayout =  inflater.inflate(R.layout.fragment_sorter_page, container, false)

        //обращение к элементам через макет
        fragmentlayout.btn_sorter.setOnClickListener {
            sortNumbers( fragmentlayout.et_sorter.text.toString())
        }
        //ввод только цифр и пробела
        /*fragmentlayout.et_sorter.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                return@OnKeyListener true
            }
            false
        })*/

        //скрыть результат перез показом
        fragmentlayout.tv_sort_result.visibility = View.GONE
        fragmentlayout.tv_sort_result_desc.visibility = View.GONE



        return  fragmentlayout
    }


    private fun sortNumbers(str : String){

        //убрать буквы из строки
        var re = Regex("[A-Za-z]")
        var numbers = re.replace(str, "")

        //удалить двойные пробелы
        numbers = numbers.replace("\\s+".toRegex(), " ")
//        numbers = numbers.replace("\\s+", " ")
//        re = Regex("[/s+]")
//        numbers = re.replace(numbers, " ")
        println(numbers)


        var strsArr = numbers.split(" ").toMutableList()
        //strsArr = strsArr.filterNot { it == " " || it == null}.toMutableList()

         //только числа
        for (i in 0 until strsArr.size) {
            strsArr[i] = strsArr[i].toIntOrNull().toString()
        }



       /* val regex = "\\d".toRegex()

        strsArr.forEach {
            regex.find(it)
            //etIntValueFromString(it)
            println(" $it ")
        }*/

        //strsArr.sort()
        println("Отсортированный: ${strsArr.sorted()}")

        tv_sort_result.text = getString(
            R.string.tv_sort_result,
            strsArr.sorted()
        )
        tv_sort_result_desc.text = getString(
            R.string.tv_sort_result_desc,
            strsArr.sortedDescending()
        )
        //
        tv_sort_result.visibility = View.VISIBLE
        tv_sort_result_desc.visibility = View.VISIBLE

    }

    fun getIntValueFromString(value : String): Int {
        var returnValue = ""
        value.forEach {
            val item = it.toString().toIntOrNull()
            if(item is Int){
                returnValue += item.toString()
            }

        }
        return returnValue.toInt()

    }



}


