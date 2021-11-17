package com.jjfs.android.composetestapp.ui.screens.auth

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .background(MaterialTheme.colors.surface)
        ) {
            Text(
                text = "Please enter your pin number",
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                isError = state.error.isNotEmpty(),
                modifier = Modifier.testTag("pinInput"),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colors.primary,
                    textColor = MaterialTheme.colors.primary
                )
            )
            Spacer(modifier = Modifier.padding(16.dp))

            LoginButton(
                onClick = { viewModel.doLogin() },
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
