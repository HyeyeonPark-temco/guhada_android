package io.temco.guhada.data.db.dao

import androidx.room.*
import io.temco.guhada.data.db.entity.BrandEntity

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 DAO
 *
 */
@Dao
interface BrandDao {

    @Query("SELECT * FROM brand_data WHERE nameKo is null or nameKo = '' ORDER By b_id")
    fun getEnAll() : List<BrandEntity>

    @Query("SELECT * FROM brand_data WHERE nameKo is not null and  nameKo != '' ORDER By b_id")
    fun getKoAll() : List<BrandEntity>

    @Query("SELECT * FROM brand_data ORDER By b_id")
    fun getAll() : List<BrandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity : BrandEntity)

    @Query("DELETE from brand_data")
    fun deleteAll()

    @Delete
    fun delete(entity : BrandEntity)


}