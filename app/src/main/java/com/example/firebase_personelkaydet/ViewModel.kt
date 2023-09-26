package com.example.firebase_personelkaydet

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.InputStream

class myViewModel: ViewModel(){

    var firebaseAuth = Firebase.auth
    private var databaseReference = Firebase.database.getReference("users")

    val personelList = ArrayList<Personel>()

    private val postListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            personelList.clear()
            for (i in snapshot.children) {

                val pNameSurname = i.child("pnameSurname").value.toString()
                val pNo = i.child("pno").value.toString()
                val pSalary = i.child("psalary").value.toString()
                val personel = Personel(pNo,pNameSurname,pSalary)

                personelList.add(personel)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("Hata","Veri Tabanı Hatası")
        }
    }
    init {
        databaseReference.addValueEventListener(postListener)
    }
    fun writeNewPersonel(pNo:String,pNameSurname:String,pSalary:String){
        val user = Personel(pNo, pNameSurname, pSalary)
        databaseReference.child(pNo).setValue(user)
    }

}