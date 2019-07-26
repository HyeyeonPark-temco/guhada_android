package io.temco.guhada.data.db.dao

import androidx.room.*
import io.temco.guhada.data.db.entity.RecentDealEntity

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 DAO
 *
 */
@Dao
interface RecentDealDao {

    @Query("SELECT * FROM recent_deal ORDER By insert_date DESC LIMIT :limitNum")
    fun getAll(limitNum : Int) : List<RecentDealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deal : RecentDealEntity)

    @Query("DELETE from recent_deal")
    fun deleteAll()

    @Query("DELETE from recent_deal WHERE dealId == :dealId")
    fun delete(dealId : Long)

    @Delete
    fun delete(deal : RecentDealEntity)

}