package io.temco.guhada.data.model.search

import io.temco.guhada.data.db.entity.SearchWordEntity

enum class SearchWordType {RECENT, POPULAR, AUTOCOMPLETE}

open class SearchWord(var type : SearchWordType){
    override fun toString(): String {
        return "SearchWord(type=$type)"
    }
}


data class SearchRecent(var recent: SearchWordEntity) : SearchWord(SearchWordType.RECENT){
    override fun toString(): String {
        return "SearchRecent(recent=$recent)"
    }
}

data class SearchPopular(var popular: Keywords) : SearchWord(SearchWordType.POPULAR){
    override fun toString(): String {
        return "SearchPopular(popular=$popular)"
    }
}

data class SearchAutoComplete(var name: String, var searchWord : String) : SearchWord(SearchWordType.AUTOCOMPLETE){
    override fun toString(): String {
        return "SearchAutoComplete(name='$name', searchWord='$searchWord')"
    }
}