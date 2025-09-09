package com.frankegan.verdant.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frankegan.verdant.data.ImgurImage

@Dao
interface ImageDao {

    @Query("SELECT * FROM image")
    suspend fun getAll(): List<ImgurImage>

    @Query("SELECT * FROM image WHERE id LIKE :id")
    fun getImage(id: String): ImgurImage?

    @Query("SELECT * FROM image")
    fun getAllPages(): PagingSource<Int, ImgurImage>

    @Query("UPDATE image SET favorite = :favorite WHERE id = :id")
    fun updateFavorited(id: String, favorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: ImgurImage)

    @Delete
    fun delete(user: ImgurImage)

    @Query("DELETE FROM image")
    fun deleteImages()
}