package com.vanitap.myfamilysafety

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class LoginFragment : Fragment() {
    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var btnLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.login_username)
        password = view.findViewById(R.id.login_password)
        fAuth = Firebase.auth
        btnLogin = view.findViewById(R.id.btn_login) // Initialize btnLogin

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        btnLogin.setOnClickListener {
            validateEmptyForm()
        }

        return view
    }

    private fun firebaseSignIn() {
        btnLogin.isEnabled = false
        btnLogin.alpha = 0.5f

        fAuth.signInWithEmailAndPassword(username.text.toString(), password.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(), addToStack = true)
            } else {
                btnLogin.isEnabled = true
                btnLogin.alpha = 1.0f
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmptyForm() {
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter UserName", icon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", icon)
            }
            else -> {
                if (username.text.toString().matches(Regex(pattern = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
                    if (password.text.toString().length >= 5) {
                        firebaseSignIn()
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    } else {
                        password.setError("Please enter at least 5 characters", icon)
                    }
                } else {
                    username.setError("Please enter a valid Email Id", icon)
                }
            }
        }
    }
}
