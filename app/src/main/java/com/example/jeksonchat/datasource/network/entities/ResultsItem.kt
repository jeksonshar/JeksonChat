package com.example.jeksonchat.datasource.network.entities

import com.google.gson.annotations.SerializedName

data class ResultsItem(

	@field:SerializedName("message_id")
	val messageId: String? = null
)