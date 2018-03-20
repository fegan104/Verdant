package com.frankegan.verdant.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.frankegan.verdant.models.ImgurUser

/**
 * Created by frankegan on 3/6/18.
 */
@Database(entities = [ImgurUser::class], version = 1)
abstract class VerdantDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}