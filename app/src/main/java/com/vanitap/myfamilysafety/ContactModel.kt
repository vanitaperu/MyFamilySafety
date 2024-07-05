package com.vanitap.myfamilysafety

import androidx.room.Entity

@Entity
data class ContactModel(
    val name:String,
    val number:String

)
