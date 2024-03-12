package com.testing.githubusersdk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.testing.githubusersdk.data.locale.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE function = :function")
    suspend fun remoteKeyByQuery(function: String): RemoteKey

    @Query("DELETE FROM remote_keys WHERE function = :function")
    suspend fun deleteByQuery(function: String)

}