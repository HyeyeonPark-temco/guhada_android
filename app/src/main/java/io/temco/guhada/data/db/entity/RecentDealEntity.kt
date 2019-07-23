package io.temco.guhada.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 Entity - 임시
 *
 */
@Entity(tableName = "recentdeal")
data class RecentDealEntity (@PrimaryKey(autoGenerate = true) val idx : Int,
                             @ColumnInfo(name = "insert_date") val date : String,
                             @ColumnInfo(name = "data") val data : String)