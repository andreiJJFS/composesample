package com.jjfs.android.composetestapp.ui.utils

import com.jjfs.android.composetestapp.ui.Screen

sealed class Event {
    data class Navigate<T>(val screen: Screen, val args: T? = null): Event()
    data class OpenLogin(val callback: () -> Unit = {}): Event()
    object DismissLogin: Event()
    data class Toast(val message: String): Event()
    data class SetTitle(val title: String): Event()
    data class ShowAlertDialog(val message: String? = null): Event()
    data class ShowSnackBar(val message: String): Event()
}