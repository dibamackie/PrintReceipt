package com.example.diba_print.models

data class PrintOrder(
    val type: String,
    val size: String,
    val quantity: Int,
    val phoneNumber: String,
    val discountCode: String?,
    val delivery: Boolean
)
