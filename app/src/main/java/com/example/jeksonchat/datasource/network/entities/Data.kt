package com.example.jeksonchat.datasource.network.entities

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("recipientId")
	val recipientId: String? = "",

	@field:SerializedName("senderId")
	val senderId: String? = "",

	@field:SerializedName("title")
	val title: String? = "",

	@field:SerializedName("body")
	val body: String? = ""
)