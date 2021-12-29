package com.example.manageyourinventoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SignUplink.setOnClickListener {
            var signuppage = Intent(this, SignUp::class.java)
            startActivity(signuppage)

        }


        auth = FirebaseAuth.getInstance()
        LoginButton.setOnClickListener {
           loginUser()
        }

    }

    private fun loginUser(){
        val email: String = Log_email.text.toString()
        val password: String = Log_password.text.toString()

        if (email.equals("")){
            Toast.makeText(this,"Please enter your Email", Toast.LENGTH_LONG).show()
        }else if(password.equals("")){
            Toast.makeText(this,"Please enter your Password", Toast.LENGTH_LONG).show()


        }else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task->
                if (task.isSuccessful){
                    var main = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(main)
                    finish()

                }else{
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()

                }
            }
        }


        }

}