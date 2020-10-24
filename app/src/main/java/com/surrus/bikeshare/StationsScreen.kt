package com.surrus.bikeshare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StationsScreen() {
    var bottomBarSelectedIndex by remember { mutableStateOf(0) }

    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val stationsState = bikeShareViewModel.stations.collectAsState()

    Scaffold(
        bodyContent = { innerPadding ->
            LazyColumnFor(
                contentPadding = innerPadding,
                items = stationsState.value) { station ->
                StationView(station)
            }
        },
        bottomBar = {
            BottomAppBar(content = {
                BottomNavigationItem(icon = { Icon(Icons.Default.LocationOn) }, label = { Text("Galway") },
                    selected = bottomBarSelectedIndex == 0,
                    onClick = {
                        bottomBarSelectedIndex = 0
                        bikeShareViewModel.setCity("galway")
                    })
                BottomNavigationItem(icon = { Icon(Icons.Default.LocationOn) }, label = { Text("Oslo") },
                    selected = bottomBarSelectedIndex == 1,
                    onClick = {
                        bottomBarSelectedIndex = 1
                        bikeShareViewModel.setCity("oslo-bysykkel")
                    })
            })
        }
    )
}


@Composable
fun StationView(station: Station) {
    Card(elevation = 12.dp, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Image(asset = vectorResource(R.drawable.ic_bike),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.preferredSize(32.dp))

            Spacer(modifier = Modifier.preferredSize(16.dp))

            Column {
                Text(text = station.name, style = MaterialTheme.typography.h6)

                val textStyle = MaterialTheme.typography.body2
                Row {
                    Text("Free:", modifier = Modifier.width(48.dp), style = textStyle)
                    Text(text = station.freeBikes().toString(), style = textStyle)
                }
                Row {
                    Text("Slots:", modifier = Modifier.width(48.dp), style = textStyle)
                    Text(text = station.slots().toString(), style = textStyle)
                }
            }
        }
    }
}

