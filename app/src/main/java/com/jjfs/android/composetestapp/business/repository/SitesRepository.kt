package com.jjfs.android.composetestapp.business.repository

import com.apollographql.apollo.coroutines.toFlow
import com.jjfs.android.composetestapp.LaunchDetailsQuery
import com.jjfs.android.composetestapp.LaunchListQuery
import com.jjfs.android.composetestapp.TripsBookedSubscription
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.operationResultFrom
import com.jjfs.android.composetestapp.business.network.ApolloApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

const val wsUrl = "wss://apollo-fullstack-tutorial.herokuapp.com/graphql"

class SitesRepository(private val apolloApiService: ApolloApiService) {

    val bookingsFlow = apolloApiService.client.subscribe(TripsBookedSubscription()).toFlow()

    suspend fun fetchSites(cursor: String = ""): OperationResult<List<Site>, Exception> {
        return operationResultFrom { apolloApiService.fetchSites(cursor) }
    }

    fun fetchSiteDetail(id: String): Flow<OperationResult<LaunchDetails, Exception>> = flow {
        emit(OperationResult.InProgress())
        val response = operationResultFrom { apolloApiService.fetchSiteDetails(id) }
        kotlinx.coroutines.delay(1000)
        emit(response)
    }

    fun bookTrip(id: String): Flow<OperationResult<Any, Exception>> = flow {
        emit(OperationResult.InProgress())
        val response = operationResultFrom { apolloApiService.book(id) }
        emit(response)
    }

    fun cancelTrip(id: String): Flow<OperationResult<Any, Exception>>  = flow {
        emit(OperationResult.InProgress())
        val response = operationResultFrom { apolloApiService.cancel(id) }
        emit(response)
    }
}

@Serializable
data class Site(
    val id: String,
    val name: String,
    val mission: Mission
) {
    constructor(launch: LaunchListQuery.Launch): this(
        id = launch.id,
        name = launch.site ?: "",
        mission = launch.mission?.let { Mission(it) } ?: Mission()
    )
}

@Serializable
data class Mission(
    val name: String = "Placeholder",
    val missionPatch: String = ""
) {
    constructor(mission : LaunchListQuery.Mission): this(
        name =  mission.name ?: "",
        missionPatch = mission.missionPatch ?: ""
    )

    constructor(mission : LaunchDetailsQuery.Mission): this(
        name =  mission.name ?: "",
        missionPatch = mission.missionPatch ?: ""
    )
}

data class LaunchDetails(
    val id: String,
    val name: String,
    val mission: Mission,
    val rocket: Rocket,
    val isBooked: Boolean
) {
    constructor(launch: LaunchDetailsQuery.Launch): this(
        id = launch.id,
        name = launch.site ?: "",
        mission = Mission(launch.mission ?: throw Exception()),
        rocket = Rocket(launch.rocket ?: throw Exception()),
        isBooked = launch.isBooked
    )
}

data class Rocket(
    val name: String,
    val type: String
) {
    constructor(rocket: LaunchDetailsQuery.Rocket): this(
        name =  rocket.name ?: "",
        type = rocket.type ?: ""
    )
}

