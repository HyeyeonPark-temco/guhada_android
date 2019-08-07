package io.temco.guhada.data.model.search

import com.google.gson.annotations.SerializedName

open class AutoComplete{

   /* var brands: List<Brands> = arrayListOf()
    var categories: List<Categories> = arrayListOf()
    var products: List<Products> = arrayListOf()*/

    @SerializedName("name")
    var name : List<String> = arrayListOf()

}

/*

data class Brands(var id : Int,
                  var nameDefault : String,
                  var nameEn : String,
                  var nameKo : String)

data class Categories(var id : Int,
                  var name : String,
                  var hierarchy : String)

data class Products(var id : Int,
                      var name : String)*/
