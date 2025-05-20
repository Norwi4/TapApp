package com.norwi.tapapp2

data class Product(
    val id: Long,
    var name: String,
    var isBought: Boolean = false
)