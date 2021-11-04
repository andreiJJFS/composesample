package com.jjfs.android.composetestapp.ui.screens.detail

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.ui.components.BaseScaffold
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialApi
@Composable
fun DetailScreen(
    onNav: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    openDrawer: () -> Unit,
    order: Order,
    viewModel: DetailViewModel = getViewModel(),
) {
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = 1) {
        viewModel.setOrder(order)
        viewModel.getCachedOrder()
    }

    BaseScaffold(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        showBottomSheet = { /*TODO*/ },
        onDismissCallback = { /*TODO*/ },
        openDrawer = openDrawer
    ) {
        DetailScreenContent(
            order = state.order,
            navBack = onNavigateUp,
            onSubmit = { description: String -> viewModel.updateDescription(description) },
            description = state.description
        )
    }
}

@Composable
fun DetailScreenContent(
    order: Order,
    navBack: () -> Unit,
    onSubmit: (String) -> Unit,
    description: String,

) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        DetailCard(order = order)
        DescriptionCard(description = description, onSubmit = onSubmit)
        DetailBackButton { navBack() }
    }
}

@Composable
fun DetailCard(order: Order) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        elevation = animateDpAsState(8.dp).value,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            DetailCardContent(order)
        }
    }
}

@Composable
fun DetailCardContent(order: Order) {
    Text(text = order.orderId, modifier = Modifier.padding(8.dp), fontSize = 24.sp)
    Spacer(modifier = Modifier.padding(8.dp))
    Text(text = order.supplierName, modifier = Modifier.padding(8.dp), fontSize = 16.sp)
    Spacer(modifier = Modifier.padding(8.dp))
    Text(text = "Ordered Quantity: ${order.itemQuantity}", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
    Spacer(modifier = Modifier.padding(8.dp))
    Text(text = order.orderDate, modifier = Modifier.padding(8.dp), fontSize = 16.sp)
}

@Composable
fun DescriptionCard(description: String, onSubmit: (String) -> Unit) {
    var descriptionTextInput by remember { mutableStateOf("") }
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = animateDpAsState(8.dp).value,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Description: $description")
            Spacer(modifier = Modifier.padding(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = descriptionTextInput, onValueChange = { descriptionTextInput = it })
                Button(onClick = { onSubmit(descriptionTextInput) }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Submit",
                        modifier = Modifier.padding(4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DetailBackButton(navBack: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Button(onClick = navBack, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Back", modifier = Modifier.padding(8.dp))
        }
    }
}