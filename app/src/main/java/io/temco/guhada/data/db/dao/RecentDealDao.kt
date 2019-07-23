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

    @Query("SELECT * FROM recentdeal ORDER By idx DESC")
    fun getAll() : List<RecentDealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deal : RecentDealEntity)

    @Query("DELETE from recentdeal")
    fun deleteAll()

    @Query("DELETE from recentdeal WHERE idx == :num_list")
    fun delete(num_list : String)

    @Delete
    fun delete(deal : RecentDealEntity)

}