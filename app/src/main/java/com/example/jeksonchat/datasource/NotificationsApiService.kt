package com.example.jeksonchat.datasource

import com.example.jeksonchat.datasource.network.entities.FirebaseNotification
import com.example.jeksonchat.datasource.network.entities.ResponseNotification
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationsApiService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAwMyobRk:APA91bFB6HBLHMbqwXPrQ_rtQ0U2A4BMauCzEkafYoCPgDGH6vBPtwu_42sdDOwAwZABO5Ae43UYggLYaSCsjv5tDU5wvJJ1cbmTwgZ2iR4Fpus-MlJtiUoSrFmsaT2aeNk2BCgcsOYH"
    )
    @POST("/fcm/send")
    suspend fun sendNotifications(@Body notificationEntity: FirebaseNotification): ResponseNotification
}