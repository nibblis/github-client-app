package app.test.githubclient.data.repository

import android.os.Environment
import app.test.githubclient.data.api.GithubApi
import app.test.githubclient.data.db.DownloadDao
import app.test.githubclient.data.model.Download
import app.test.githubclient.data.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: GithubApi,
    private val downloadDao: DownloadDao,
) {
    fun getAllDownloads() = downloadDao.getAllDownloads()

    suspend fun searchRepositories(username: String): Result<List<Repository>> {
        return try {
            Result.success(api.getUserRepositories(username))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadRepository(owner: String, repoName: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.downloadRepository(owner, repoName)
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, "${owner}_${repoName}.zip")

                response.byteStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                downloadDao.insertDownload(
                    Download(
                        username = owner,
                        repositoryName = repoName,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteDownload(download: Download) {
        downloadDao.deleteDownload(download)
    }

    suspend fun isRepositoryDownloaded(repository: Repository): Boolean {
        return downloadDao.isRepositoryDownloaded(
            username = repository.owner.login,
            repositoryName = repository.name
        )
    }
}