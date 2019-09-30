package io.temco.guhada.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.temco.guhada.data.model.Category
import java.io.Serializable

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 Entity
 *
 * data = product JsonObject String
 *
 */
enum class CategoryLabelType {Women, Men, Kids}

@Entity(tableName = "category_data")
data class CategoryEntity (@PrimaryKey(autoGenerate = true) val idx : Int,
                           @ColumnInfo(name = "c_id") var id : Int,
                           @ColumnInfo(name = "label") var label : String,
                           @ColumnInfo(name = "key") var key : String,
                           @ColumnInfo(name = "hierarchy") var hierarchy : String,
                           @ColumnInfo(name = "fullDepthName") var fullDepthName : String,
                           @ColumnInfo(name = "depth") var depth : Int,
                           @ColumnInfo(name = "immediateChildrenCount") var immediateChildrenCount : Int,
                           @ColumnInfo(name = "endHierarchies") var endHierarchies : Int,
                           @ColumnInfo(name = "title") var title : String,
                           @ColumnInfo(name = "isUnisex") var isUnisex : Boolean) : Serializable {

    constructor():this(0,0,"","","","",0,0,0,"",false)

    constructor(catergory : Category,label : String, depth : Int, endHierarchies : Int):this(
            0,catergory.id, label,catergory.key,catergory.hierarchy,catergory.fullDepthName,depth,catergory.immediateChildrenCount,endHierarchies,catergory.title,catergory.isUnisex)

    fun initData(id:Int, label: String, key: String, hierarchy: String, fullDepthName: String, depth:Int, immediateChildrenCount:Int, endHierarchies:Int, title: String, isUnisex : Boolean):CategoryEntity{
        this.id = id
        this.label = label
        this.key = key
        this.hierarchy = hierarchy
        this.fullDepthName = fullDepthName
        this.depth = depth
        this.immediateChildrenCount = immediateChildrenCount
        this.endHierarchies = endHierarchies
        this.title = title
        this.isUnisex = isUnisex
        return this
    }



}