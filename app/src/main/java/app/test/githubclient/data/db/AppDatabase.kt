package app.test.githubclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.test.githubclient.data.model.Download

@Database(entities = [Download::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}