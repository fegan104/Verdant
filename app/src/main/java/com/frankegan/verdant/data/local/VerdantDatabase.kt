package com.frankegan.verdant.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.frankegan.verdant.VerdantApp
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurUser



/**
 * Created by frankegan on 3/6/18.
 */
@Database(
    version = 2,
    exportSchema = false,
    entities = [ImgurUser::class, ImgurImage::class],
)
abstract class VerdantDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: VerdantDatabase? = null

        @JvmStatic
        fun getInstance(): VerdantDatabase {
            if (INSTANCE == null) {
                synchronized(VerdantDatabase::javaClass) {
                    INSTANCE = Room
                            .databaseBuilder(VerdantApp.instance, VerdantDatabase::class.java, "verdant-database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}