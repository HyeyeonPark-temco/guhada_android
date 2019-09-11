package io.temco.guhada.data.db.dao

import androidx.room.*
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.db.entity.RecentDealEntity

/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 DAO
 *
 */
@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_data WHERE label == :label AND depth =:depth ORDER By c_id")
    fun getDepthAll(label : String, depth : Int) : List<CategoryEntity>

    @Query("SELECT * FROM category_data ORDER By c_id")
    fun getAll() : List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity : CategoryEntity)

    @Query("DELETE from category_data")
    fun deleteAll()

    @Delete
    fun delete(entity : CategoryEntity)

}