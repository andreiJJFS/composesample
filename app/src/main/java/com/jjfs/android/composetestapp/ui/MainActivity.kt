package com.jjfs.android.composetestapp.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jjfs.android.composetestapp.ui.theme.ComposeTestAppTheme
import com.jjfs.android.composetestapp.ui.utils.ConnectionState
import com.jjfs.android.composetestapp.ui.utils.currentConnectivityState
import com.jjfs.android.composetestapp.ui.utils.observeConnectivityAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
//@ExperimentalAnimationApi
class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            MainApp()
            Surface(color = MaterialTheme.colors.background) {
                Greeting("Android")
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(_context: Context? = null): State<ConnectionState> {
    val context = _context ?: LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collectLatest {
            value = it
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
