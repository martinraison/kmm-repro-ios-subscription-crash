package com.example.repro

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo3.network.ws.ApolloWebSocketNetworkTransport
import com.example.repro.graphql.CancelTripMutation
import com.example.repro.graphql.TripsBookedSubscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch

class Greeting : CoroutineScope by MainScope() {

    private val apolloClient = ApolloClient(
        networkTransport = ApolloHttpNetworkTransport(
            serverUrl = "https://apollo-fullstack-tutorial.herokuapp.com/graphql",
            headers = mapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json",
            )
        ),
        subscriptionNetworkTransport = ApolloWebSocketNetworkTransport("wss://apollo-fullstack-tutorial.herokuapp.com/graphql", this)
    )

    fun greeting(): Flow<String> {
        launch {
            for (i in 1..1000) {
                delay(1000)
                println("cancelling trip $i")
                apolloClient.mutate(CancelTripMutation())
            }
        }
        return apolloClient.subscribe(TripsBookedSubscription()).withIndex().map { response ->
            "result: ${response.value.data?.tripsBooked}, iter: ${response.index}"
        }.filterNotNull()
    }
}