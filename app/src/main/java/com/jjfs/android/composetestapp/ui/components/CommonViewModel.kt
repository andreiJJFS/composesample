package com.jjfs.android.composetestapp.ui.components

import androidx.lifecycle.ViewModel
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class CommonViewModel: ViewModel() {
    val _eventChannel = MutableSharedFlow<Event>()
    val eventChannel = _eventChannel.asSharedFlow()
}