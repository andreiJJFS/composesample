package com.jjfs.android.composetestapp.business.repository

import android.content.Context
import android.util.Log
import com.jjfs.android.composetestapp.ui.utils.observeConnectivityAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkService(context: Context, scope: CoroutineScope) {

    private val connectionState = context.observeConnectivityAsFlow()

    init {
        scope.launch {
            connectionState.collectLatest {
                Log.i("Network Service", it.toString())
            }
        }
    }
}
