package io.temco.guhada.data.db.dao

import androidx.room.*
import io.temco.guhada.data.db.entity.RecentDealEntity

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 DAO - 임시
 *
 */
@Dao
interface RecentDealDao {

    @Query("SELECT * FROM recent_deal ORDER By insert_date DESC LIMIT 20")
    fun getAll() : List<RecentDealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deal : RecentDealEntity)

    @Query("DELETE from recent_deal")
    fun deleteAll()

    @Query("DELETE from recent_deal WHERE idx == :dealId")
    fun delete(dealId : Long)

    @Delete
    fun delete(deal : RecentDealEntity)

}