package com.testing.githubusersdk.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val FUNCTION_USERS = "users"
const val FUNCTION_SEARCH = "search"

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    @ColumnInfo(name = "function")
    val function: String,
    @ColumnInfo(name = "next_key")
    val nextKey: String?
)