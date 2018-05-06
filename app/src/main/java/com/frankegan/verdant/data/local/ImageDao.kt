package com.frankegan.verdant.data.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.frankegan.verdant.data.ImgurImage

interface ImageDao {
    fun getAll(): LiveData<List<ImgurImage>>

    fun getImage(id: String): LiveData<ImgurImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: ImgurImage)

    @Delete
    fun delete(user: ImgurImage)
}