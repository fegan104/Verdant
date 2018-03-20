package com.frankegan.verdant.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.frankegan.verdant.models.ImgurUser


/**
 * Created by frankegan on 3/6/18.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<ImgurUser>>

    @Query("SELECT username FROM user LIMIT 1")
    fun getUsername(): LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: ImgurUser)

    @Delete
    fun delete(user: ImgurUser)
}