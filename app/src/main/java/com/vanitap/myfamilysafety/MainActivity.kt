package com.vanitap.myfamilysafety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vanitap.myfamilysafety.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentNavigation {

    private lateinit var fAuth: FirebaseAuth

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS
    )

    private val permissionCode = 78

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fAuth = Firebase.auth

        askForPermission()

        val currentUser = fAuth.currentUser

        if (currentUser != null) {
            inflateFragment(HomeFragment())
            saveUserData(currentUser)
        } else {
            inflateFragment(LoginFragment())
        }

        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_bar)
        binding.bottomBar.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.nav_guard -> {
                    inflateFragment(GuardFragment.newInstance())
                }
                R.id.nav_home -> {
                    inflateFragment(HomeFragment.newInstance())
                }
                R.id.nav_profile -> {
                    inflateFragment(ProfileFragment.newInstance())
                }
                R.id.nav_dashboard -> {
                    inflateFragment(MapsFragment())
                }
                R.id.nav_camera -> {
                    inflateFragment(CameraFragment())
                }
            }
            true
        }

    }

    private fun saveUserData(currentUser: FirebaseUser) {
        val name = currentUser.displayName ?: "N/A"
        val mail = currentUser.email ?: "N/A"
        val phoneNumber = currentUser.phoneNumber ?: "N/A"
        val imageUrl = currentUser.photoUrl?.toString() ?: "N/A"

        val db = Firebase.firestore

        val user = hashMapOf(
            "name" to name,
            "mail" to mail,
            "phoneNumber" to phoneNumber,
            "imageUrl" to imageUrl
        )

        db.collection("users")
            .document(mail)
            .set(user)
            .addOnSuccessListener {
                // Handle success
            }.addOnFailureListener {
                // Handle failure
            }
    }

    private fun askForPermission() {
        ActivityCompat.requestPermissions(this, permissions, permissionCode)
    }

    private fun inflateFragment(newInstance: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance)
        transaction.commit()
    }





    private fun allPermissionGranted(): Boolean {
        for (item in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    item
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
        if (addToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun takePicture() {
        TODO("Not yet implemented")
    }


}
