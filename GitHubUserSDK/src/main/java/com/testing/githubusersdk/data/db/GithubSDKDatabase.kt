package com.testing.githubusersdk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testing.githubusersdk.data.dao.GithubUserDao
import com.testing.githubusersdk.data.dao.RemoteKeyDao
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.RemoteKey

const val DATABASE_NAME = "GithubSDKDB"

@Database(
    entities = [GitHubUserEntity::class, GitHubUserProfileEntity::class, RemoteKey::class],
    version = 1
)
abstract class GithubSDKDatabase : RoomDatabase() {
    abstract fun githubUserDao(): GithubUserDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}