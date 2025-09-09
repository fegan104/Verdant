package com.frankegan.verdant.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frankegan.verdant.data.ImgurUser


/**
 * Created by frankegan on 3/6/18.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<ImgurUser>

    @Query("SELECT username FROM user LIMIT 1")
    fun getUsername(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: ImgurUser)

    @Delete
    fun delete(user: ImgurUser)
}