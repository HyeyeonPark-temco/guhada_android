package io.temco.guhada.data.db.dao

import androidx.room.*
import io.temco.guhada.data.db.entity.SearchWordEntity


/**
 * @author park jungho
 * 19.07.22
 *
 * 최근본 상품 DAO
 *
 */
@Dao
interface SearchWordDao {

    @Query("SELECT * FROM search_word ORDER By insert_date DESC LIMIT :limitNum")
    fun getAll(limitNum : Int) : List<SearchWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word : SearchWordEntity)

    @Query("DELETE from search_word")
    fun deleteAll()

    @Query("DELETE from search_word WHERE search_word == :search_word")
    fun delete(search_word : String)

    @Delete
    fun delete(word : SearchWordEntity)

}