package app.test.githubclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class Download(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val repositoryName: String,
    val timestamp: Long
)