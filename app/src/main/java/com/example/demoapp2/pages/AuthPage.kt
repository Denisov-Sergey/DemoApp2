package com.example.demoapp2.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.demoapp2.R
import kotlinx.android.synthetic.main.fragment_auth_page.view.*

class AuthPage : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // ссылка на макет фрагмента
        val fragmentlayout =  inflater.inflate(R.layout.fragment_auth_page, container, false)

        //ссылка на навигационный контроллер
        val navController =   NavHostFragment.findNavController(this)

        //слушатели кнопок
        fragmentlayout.btn_nav_auth.setOnClickListener{navController.navigate(R.id.authPage)}
        fragmentlayout.btn_nav_list.setOnClickListener{navController.navigate(R.id.listPage)}


        return  fragmentlayout

    }

    companion object {

    }
}