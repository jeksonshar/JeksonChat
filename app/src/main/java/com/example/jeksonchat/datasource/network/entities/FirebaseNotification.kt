package com.example.jeksonchat.datasource.network.entities

import com.google.gson.annotations.SerializedName

data class FirebaseNotification(

	@field:SerializedName("data")
	val data: Data? = Data(),

	@field:SerializedName("registration_ids")
	val registrationIds: List<String?>? = emptyList()
)