package com.example.chatappfirebase.ui.common

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

open class BaseFragment : Fragment() {

    protected fun navigateToPage(actionId : Int) {
        findNavController().navigate(actionId)
    }

    protected fun showLoading(isShow : Boolean) {

    }

    protected fun hideLoading() {


    }
}