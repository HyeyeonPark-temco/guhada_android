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
@Entity(tableName = "recent_deal")
data class RecentDealEntity (@PrimaryKey(autoGenerate = true) val idx : Int,
                             @ColumnInfo(name = "insert_date") var date : Long,
                             @ColumnInfo(name = "dealId") var dealId : Long,
                             @ColumnInfo(name = "dealData") var data : String,
                             @ColumnInfo(name = "user_data") var userData : String) : Serializable{

    constructor():this(0,0L,0L,"","")

    fun initData(date:Long, dealId: Long, data: String, userData: String):RecentDealEntity{
        this.date = date
        this.dealId = dealId
        this.data = data
        this.userData = userData
        return this
    }

}