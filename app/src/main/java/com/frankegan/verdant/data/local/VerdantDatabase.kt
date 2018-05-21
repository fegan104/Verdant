package com.frankegan.verdant.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.frankegan.verdant.VerdantApp
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurUser



/**
 * Created by frankegan on 3/6/18.
 */
@Database(entities = [ImgurUser::class, ImgurImage::class], version = 2)
abstract class VerdantDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: VerdantDatabase? = null

        @JvmStatic
        fun getInstance(): VerdantDatabase {
            if (VerdantDatabase.INSTANCE == null) {
                synchronized(VerdantDatabase::javaClass) {
                    VerdantDatabase.INSTANCE = Room
                            .databaseBuilder(VerdantApp.instance, VerdantDatabase::class.java, "verdant-database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return VerdantDatabase.INSTANCE!!
        }

        fun clearInstance() {
            VerdantDatabase.INSTANCE = null
        }
    }
}