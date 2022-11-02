package com.example.capstone

class MenuClicked{
    var isBeverages: Boolean? = null
    var isMeal: Boolean? = null

    fun clickedBeverages(clicked: Boolean){
        this.isBeverages = clicked
    }
    fun clickedMeal(clicked: Boolean){
        this.isMeal = clicked
    }

}

