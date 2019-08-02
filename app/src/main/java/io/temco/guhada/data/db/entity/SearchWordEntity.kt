package io.temco.guhada.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


/**
 * @author park jungho
 * 19.08.01
 *
 * 최근 검색어
 *
 * data = product JsonObject String
 *
 */
@Entity(tableName = "search_word")
data class SearchWordEntity (@PrimaryKey(autoGenerate = true) val idx : Int,
                             @ColumnInfo(name = "insert_date") var insertDate : Long,
                             @ColumnInfo(name = "search_word") var searchWord : String,
                             @ColumnInfo(name = "user_data") var userData : String,
                             @ColumnInfo(name = "temp_field1") var tempField1 : String,
                             @ColumnInfo(name = "temp_field2") var tempField2 : String,
                             @ColumnInfo(name = "temp_field3") var tempField3 : String) : Serializable {

    constructor():this(0,0L,"","","","","")

    fun initData(insertDate:Long, searchWord: String, userData: String, tempField1: String, tempField2: String, tempField3: String):SearchWordEntity{
        this.insertDate = insertDate
        this.searchWord = searchWord
        this.userData = userData
        this.tempField1 = tempField1
        this.tempField2 = tempField2
        this.tempField3 = tempField3
        return this
    }

    override fun toString(): String {
        return "SearchWordEntity(idx=$idx, insertDate=$insertDate, searchWord='$searchWord', userData='$userData', tempField1='$tempField1', tempField2='$tempField2', tempField3='$tempField3')"
    }

}