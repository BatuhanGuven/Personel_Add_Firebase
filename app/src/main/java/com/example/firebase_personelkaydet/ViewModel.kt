package com.example.firebase_personelkaydet

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyViewModel: ViewModel(){
    var firebaseUser = Firebase.auth.currentUser?.uid
    var isDark = mutableStateOf(true)
    var firebaseAuth = Firebase.auth

    private var databaseUsersReference = Firebase.database.getReference("users")

    private val _personelList = MutableStateFlow<MutableList<Personel>>(mutableListOf())
    var personelList = _personelList


    private val _isLoading  = MutableStateFlow(false)
    val isLoading = _isLoading

    private val postListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            personelList.value.clear()
            for (i in snapshot.children) {
                Log.d("batuss","bura")
                val pNameSurname = i.child("pnameSurname").value.toString()
                val pNo = i.child("pno").value.toString()
                val pSalary = i.child("psalary").value.toString()
                val pPosition = i.child("pposition").value.toString()
                val personel = Personel(pNo,pNameSurname,pSalary, pPosition)
                personelList.value.add(personel)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("Hata",error.toString())
        }
    }
    init{
        databaseUsersReference.addValueEventListener(postListener)
        viewModelScope.launch {
            delay(2000)
            isLoading.value = false
        }
    }
    fun writeNewPersonel(pNo:String,pNameSurname:String,pSalary:String,pPosition:String){
        val user = Personel(pNo, pNameSurname, pSalary,pPosition)
        databaseUsersReference.child(pNo).setValue(user)
    }

    fun deletePersonel(pNo: String) {
        databaseUsersReference.child(pNo).removeValue().addOnCompleteListener { task ->
            val currentList = _personelList.value.toMutableList()
            currentList.removeIf { pNo == it.pNameSurname}
            _personelList.value=currentList
        }
    }
}