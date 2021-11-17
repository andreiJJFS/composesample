package com.jjfs.android.composetestapp.ui.screens.graphql

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.jjfs.android.composetestapp.business.repository.LaunchDetails
import com.jjfs.android.composetestapp.ui.components.BaseScaffold
import com.jjfs.android.composetestapp.ui.components.LoadingText
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.compose.getViewModel

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun GraphqlDetailScreen(
    onNav: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    showBottomSheet: () -> Unit = {},
    scaffoldState: ScaffoldState, // = rememberScaffoldState(),
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    viewModel: GraphqlViewModel = getViewModel(),
    id: String,
) {
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = 1) {
        viewModel.getSiteDetails(id)
    }

    BaseScaffold(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        showBottomSheet = showBottomSheet,
        viewModel = viewModel,
        scaffoldState = scaffoldState,
        onNav = onNav,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                state.siteDetails?.let {
                    GraphqlDetailContent(
                        isLoading = state.isLoading,
                        siteDetails = it,
                        onClick = {
                            if(!state.isLoading) {
                                if(it.isBooked) viewModel.cancel() else viewModel.book()
                            }
                        }
                    )
                } ?: LoadingText()

            }
        }
    )
}

@Composable
fun GraphqlDetailContent(isLoading: Boolean, siteDetails: LaunchDetails, onClick: () -> Unit) {
    DetailsSiteCard(site = siteDetails)
    BookButton(isLoading = isLoading, isBooked = siteDetails.isBooked, onClick = onClick)
}

@Composable
fun DetailsSiteCard(site: LaunchDetails) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
        elevation = animateDpAsState(8.dp).value,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = site.mission.missionPatch,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = site.mission.name,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = site.name,
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Rocket Name: ${site.rocket.name}",
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Rocket Type: ${site.rocket.type}",
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Booked: ${site.isBooked}",
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun BookButton(isLoading: Boolean, isBooked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = when {
                isLoading -> "LOADING"
                isBooked -> "CANCEL"
                else -> "BOOK NOW"
            })
        }
    }
}