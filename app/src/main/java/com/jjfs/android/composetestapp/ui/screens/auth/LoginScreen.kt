package com.jjfs.android.composetestapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.flow.collect
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialApi
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = getViewModel(),
    scaffoldState: BottomSheetScaffoldState,
    showBottomSheet: () -> Unit,
    onDismiss: () -> Unit
) {
    val state = viewModel.stateFlow.value
    var text by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is Event.ShowSnackBar ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Login failed"
                    )
                is Event.DismissLogin -> {
                    onDismiss()
                    showBottomSheet()
                }
                else -> Unit
            }
        }
    }

    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            Text(text = "Please enter your pin number", modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                isError = state.error.isNotEmpty(),
            )
            Spacer(modifier = Modifier.padding(8.dp))

            LoginButton(
                onClick = { viewModel.doLogin(text) },
                isLoading = state.isLoading
            )
        }
    }
}


@Composable
fun LoginButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(onClick = onClick) {
        Text(text = if(isLoading) "loading" else "Submit")
    }
}
