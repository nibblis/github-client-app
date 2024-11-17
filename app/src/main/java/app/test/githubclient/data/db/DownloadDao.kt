package app.test.githubclient.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import app.test.githubclient.data.model.Download
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<Download>>

    @Insert
    suspend fun insertDownload(download: Download)

    @Query("SELECT EXISTS(SELECT 1 FROM downloads WHERE username = :username AND repositoryName = :repositoryName)")
    suspend fun isRepositoryDownloaded(username: String, repositoryName: String): Boolean

    @Delete
    suspend fun deleteDownload(download: Download)
}