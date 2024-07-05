package com.vanitap.myfamilysafety

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class RegisterFragment : Fragment() {

    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var cnfPassword: TextInputEditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var btnRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Initialize TextInputEditText variables
        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        cnfPassword = view.findViewById(R.id.reg_cnf_password)
        fAuth = Firebase.auth
        btnRegister = view.findViewById(R.id.btn_register_reg) // Initialize btnRegister

        // Set onClickListener for the login button
        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }

        // Set onClickListener for the register button
        btnRegister.setOnClickListener {
            validateEmptyForm()
        }

        return view
    }

    private fun firebaseSignUp() {
        btnRegister.isEnabled = false
        btnRegister.alpha = 0.5f

        fAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val navHome = activity as FragmentNavigation
                    navHome.navigateFrag(HomeFragment(), true)
                } else {
                    btnRegister.isEnabled = true
                    btnRegister.alpha = 1.0f
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
            TextUtils.isEmpty(cnfPassword.text.toString().trim()) -> {
                cnfPassword.setError("Please Enter Password Again", icon)
            }
            password.text.toString() != cnfPassword.text.toString() -> {
                cnfPassword.setError("Passwords do not match", icon)
            }
            else -> {
                if (username.text.toString().matches(Regex(pattern = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
                    if (password.text.toString().length >= 5) {
                        if (password.text.toString() == cnfPassword.text.toString()) {
                            firebaseSignUp()
                            Toast.makeText(context, "Register successful", Toast.LENGTH_SHORT).show()
                        } else {
                            cnfPassword.setError("Password didn't match", icon)
                        }
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
