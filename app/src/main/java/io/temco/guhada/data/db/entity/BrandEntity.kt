package io.temco.guhada.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "brand_data")
data class BrandEntity (@PrimaryKey(autoGenerate = true) val idx : Int,
                           @ColumnInfo(name = "b_id") var id : Int,
                           @ColumnInfo(name = "nameDefault") var nameDefault : String,
                           @ColumnInfo(name = "nameEn") var nameEn : String,
                           @ColumnInfo(name = "nameKo") var nameKo : String?,
                           @ColumnInfo(name = "isFavorite") var isFavorite : Boolean) : Serializable {

    override fun toString(): String {
        return "BrandEntity(idx=$idx, id=$id, nameDefault='$nameDefault', nameEn='$nameEn', nameKo='$nameKo', isFavorite=$isFavorite)"
    }
}