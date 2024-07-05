package com.vanitap.myfamilysafety

import androidx.fragment.app.Fragment

interface FragmentNavigation {

    fun navigateFrag(fragment: Fragment, addToStack:Boolean)
    fun takePicture()
}