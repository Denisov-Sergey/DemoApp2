package com.example.demoapp2.pages

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.example.demoapp2.MainActivity
import com.example.demoapp2.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_auth_page.*
import kotlinx.android.synthetic.main.fragment_auth_page.view.*

class AuthPage : Fragment() {


    private lateinit var auth: FirebaseAuth
    private var fragmentlayout: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //инициализация авторизации
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // ссылка на макет фрагмента
        fragmentlayout =  inflater.inflate(R.layout.fragment_auth_page, container, false)




        //получаем авторизованного или нет юзера
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)


        //ссылка на навигационный контроллер
        val navController =   NavHostFragment.findNavController(this)   //ссылки тестил
        //слушатели кнопок
        fragmentlayout!!.btn_nav_auth.setOnClickListener{navController.navigate(R.id.authPage)}   //ссылки тестил
        fragmentlayout!!.btn_nav_list.setOnClickListener{navController.navigate(R.id.listPage)}   //ссылки тестил

        //авторизация регистрация
        fragmentlayout!!.btn_login.setOnClickListener{
            signIn(fragmentlayout!!.et_email.text.toString(), fragmentlayout!!.et_password.text.toString())
        }
        fragmentlayout!!.btn_register.setOnClickListener{
            createAccount(fragmentlayout!!.et_email.text.toString(), fragmentlayout!!.et_password.text.toString())
        }




        return  fragmentlayout


    }

    private  fun createAccount(email: String, password: String){

        Log.d(TAG, "создание Аккаунта: $email")

        if (!validateForm()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "созданиеАккаунта:успешно")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "созданиеАккаунта:провалено", task.exception)
                    Toast.makeText(
                        getActivity()?.getBaseContext(), "Авторизация провалена. ${task.exception}",
                        Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

                progressBar.visibility = View.GONE
            }
    }


    private fun signIn(email: String, password: String) {
        Log.d(TAG, "вход:$email")

        if (!validateForm()) {
            return
        }

        fragmentlayout!!.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "вход:успешно")

                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "вход:провалено", task.exception)

                    Toast.makeText(
                        getActivity()?.getBaseContext(), "Авторизация провалена. ${task.exception}",
                        Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

                fragmentlayout!!.progressBar.visibility = View.GONE
            }
    }


    //проверка введенных данных
    private fun validateForm(): Boolean {
        var valid = true

        val email = et_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            et_email.error = "Обязательное."
            valid = false
        } else {
            et_email.error = null
        }

        val password = et_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            et_email.error = "Обязательное."
            valid = false
        } else {
            et_email.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        fragmentlayout!!.progressBar?.visibility = View.GONE
        if (user != null) {
            //ссылка на навигационный контроллер
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.listPage)
        }
    }

    companion object {
        private  val TAG = "DemoApp"
    }
}