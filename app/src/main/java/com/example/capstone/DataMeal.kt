package com.example.capstone

data class DataMeal(
    var MealName: String? = null,
    var Price: Long? = null,
    var Status: Boolean? = null,
    var Serving: Int? = null,
    var visibility: Boolean = false,
    var ItemCode: Any? = null,
    var ImageUrl: String? = null
)
