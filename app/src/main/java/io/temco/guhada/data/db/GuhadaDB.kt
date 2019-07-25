package io.temco.guhada.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.temco.guhada.data.db.dao.RecentDealDao
import io.temco.guhada.data.db.entity.RecentDealEntity

/**
 * @author park jungho
 * 19.07.22
 *
 * 내부 DataBase ROOM
 *
 */
@Database(entities = arrayOf(RecentDealEntity::class), version = 1, exportSchema = false)
abstract class GuhadaDB : RoomDatabase(){

    abstract fun recentDealDao() : RecentDealDao


    companion object {
        private var INSTANCE : GuhadaDB? = null

        fun getInstance(context : Context) : GuhadaDB?{
            if(INSTANCE == null){
                synchronized(GuhadaDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            GuhadaDB::class.java, "guhada_DB")
                            .fallbackToDestructiveMigration()
                            //.addCallback(sRoomDatabaseCallback)
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }

}