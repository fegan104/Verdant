package com.frankegan.verdant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frankegan.verdant.data.ImagePagingKey

@Dao
interface ImagePagingKeysDao {

    @Query("SELECT * FROM paging_key WHERE imageId = :id")
    suspend fun remoteKeysArticleId(id: String): ImagePagingKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ImagePagingKey>)

    @Query("DELETE FROM paging_key")
    suspend fun clearRemoteKeys()
}