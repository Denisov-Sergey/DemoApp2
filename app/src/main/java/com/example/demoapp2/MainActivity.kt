package com.example.demoapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_auth_page.*
import kotlinx.android.synthetic.main.fragment_auth_page.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //инициализация авторизации
        auth = FirebaseAuth.getInstance()

        //получить ссылку на navController
        //val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment? ?:return
        //val navController =  host.navController

        //так тоже можно покороче
        val navController = findNavController(R.id.nav_fragment)

        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        //toolbar.setupWithNavController(navController)
        setSupportActionBar(toolbar)

        //включение бокового меню
        val sideBar = findViewById<NavigationView>(R.id.nav_view)
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout) //можно не указывать я указал сразу в определении конфига ниже
        //пункты меню связываются в конфиг бутера, указываем drawerLayout как название файла меню
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.authPage, R.id.listPage, R.id.sorterPage), drawerLayout = findViewById(R.id.drawer_layout))
        //премениение конфига меню к боковому меню
        setupActionBarWithNavController(navController, appBarConfiguration)
        sideBar.setupWithNavController(navController)


        //toolBAr
        //var appBarConfiguration = AppBarConfiguration(navController.graph)
        //appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout = drawer_layout)

    }

    override fun onStart() {
        super.onStart()

        //получаем авторизованного или нет юзера
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)

    }


    //собственно активатор меню бокового
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    private fun updateUI(user: FirebaseUser?) {

        progressBar?.visibility = View.GONE
        if (user != null) {

            val listPage = toolbar.menu.findItem(R.id.listPage)
            listPage?.isEnabled = true


        } else {

            val listPage = toolbar.menu.findItem(R.id.listPage)
            listPage?.isEnabled = false

        }
    }


}
