package io.temco.guhada.data.model.main

import java.io.Serializable


class Keyword : Serializable{

    var id = 0
    var priority = 0
    var keyword = ""
    var url = ""
    var width = 0
    var height = 0

}

class KeywordMain(index: Int,
                  type: HomeType,
                  val title : String,
                  val listKeyword : List<Keyword>) : MainBaseModel(index, type,2)