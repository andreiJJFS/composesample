package com.jjfs.android.composetestapp.ui.screens.graphql

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.jjfs.android.composetestapp.business.repository.Site
import com.jjfs.android.composetestapp.ui.Screen
import com.jjfs.android.composetestapp.ui.components.BaseScaffold
import com.jjfs.android.composetestapp.ui.components.LoadingText
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialApi
@Composable
fun GraphqlScreen(
    onNav: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    showBottomSheet: () -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    viewModel: GraphqlViewModel = getViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()
    val lazySitesList = viewModel.sites.collectAsLazyPagingItems()

    BaseScaffold(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        onDismissCallback = { lazySitesList.refresh() },
        showBottomSheet = showBottomSheet,
        viewModel = viewModel,
        scaffoldState = scaffoldState,
        onNav = onNav,
        content = {
            GraphqlScreenContent(
                state = state,
                sites = lazySitesList,
                onClick = { id: String ->
                    viewModel.clearSite()
                    onNav("${Screen.GraphqlDetail.route}/$id")
                }
            )
        }
    )
}

@Composable
fun GraphqlScreenContent(
    state: GraphqlViewModelState,
    sites: LazyPagingItems<Site>,
    onClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        if(state.isLoading) LoadingText()
        else ListContent(sites = sites, onClick = onClick)
    }
}

@Composable
fun ListContent(
    sites: LazyPagingItems<Site>,
    onClick: (String) -> Unit)
{
    Box(modifier = Modifier.semantics { contentDescription = "list" }) {
        LazyColumn {
            items(items = sites, key = { it.id }) { site ->
                SiteCard(site = site!!, onClick = onClick)
            }
        }
    }
}

@Composable
fun SiteCard(site: Site, onClick: (String) -> Unit) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick(site.id) },
        elevation = animateDpAsState(8.dp).value,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(4.dp)
                .fillMaxWidth()
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
            }
        }
    }
}