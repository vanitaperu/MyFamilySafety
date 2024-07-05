package com.vanitap.myfamilysafety

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d4d5.myfamilySfety.InviteAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanitap.myfamilysafety.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private val listContacts: ArrayList<ContactModel> = ArrayList()
    private lateinit var inviteAdapter: InviteAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnLogOut.setOnClickListener {
            Firebase.auth.signOut()
            val navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), addToStack = false)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listMembers = listOf(
            MemberModel("Vanita", "9th building, 2nd floor, Maldiv road, Manali", "90%", "220"),
            MemberModel("Shravan", "10th building, 3rd floor, Maldiv road, Manali", "80%", "210"),
            MemberModel("Surya", "11th building, 4th floor, Maldiv road, Manali", "70%", "200"),
            MemberModel("Shruti", "12th building, 5th floor, Maldiv road, Manali", "60%", "190")
        )

        val memberAdapter = MemberAdapter(listMembers)
        binding.recyclerMember.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMember.adapter = memberAdapter

        inviteAdapter = InviteAdapter(listContacts)
        binding.recyclerInvite.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerInvite.adapter = inviteAdapter

        requestPermissions()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACTS_PERMISSION
            )
        } else {
            fetchContactsInBackground()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fetchContactsInBackground()
            } else {
                Log.d("Permission", "READ_CONTACTS permission denied")
            }
        }
    }

    private fun fetchContactsInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = fetchContacts()
            withContext(Dispatchers.Main) {
                listContacts.addAll(contacts)
                inviteAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("Range")
    private fun fetchContacts(): ArrayList<ContactModel> {
        val cr = requireActivity().contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val contactsList: ArrayList<ContactModel> = ArrayList()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNumber > 0) {
                    val phoneCursor = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id), null
                    )
                    phoneCursor?.use { pCur ->
                        while (pCur.moveToNext()) {
                            val phoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactsList.add(ContactModel(name, phoneNum))
                        }
                    }
                }
            }
        }
        return contactsList
    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 100

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
