package com.example.capstone

data class DataBeverages(
    val ItemCode: Any? = null,
    val BeverageName: String? = null,
    val Bucket: Boolean? = null,
    val Details: String? = null,
    var Price: Int? = null,
    var Quantity: Int? = null,
    val Size: Any? = null,
    var Status: Boolean? = null,
    var ImageUrl: String? = null,
    var visibility:Boolean = false,
    var oldPrice: Int? = null,
    var oldQuantity: Int? =null,
    var isSoloSelected:Boolean = false,
    var isBucketSelected:Boolean = false

){
    fun isBucket(isBucketSelected: Boolean){
        this.isBucketSelected = isBucketSelected
    }
    fun isSolo(isSolo: Boolean){
        this.isSoloSelected = isSolo
    }
    fun newOldPrice(oldPrice: Int){
        this.oldPrice = oldPrice
    }
    fun newOldQty(oldQuantity: Int){
        this.oldQuantity = oldQuantity
    }
    fun editPrice(price:Int){
        this.Price = price
    }
    fun editQuantity(qty:Int){
        this.Quantity = qty
    }
}