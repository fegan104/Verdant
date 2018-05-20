package com.frankegan.verdant.data.local

import android.arch.persistence.room.*
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