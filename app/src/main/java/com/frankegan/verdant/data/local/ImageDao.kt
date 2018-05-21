package com.frankegan.verdant.data.local

import android.arch.persistence.room.*
import com.frankegan.verdant.data.ImgurImage

@Dao
interface ImageDao {

    @Query("SELECT * FROM image")
    fun getAll(): List<ImgurImage>

    @Query("SELECT * FROM image WHERE id LIKE :id")
    fun getImage(id: String): ImgurImage?

    @Query("UPDATE image SET favorite = :favorite WHERE id = :id")
    fun updateFavorited(id: String, favorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: ImgurImage)

    @Delete
    fun delete(user: ImgurImage)

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM image") fun deleteImages()
}