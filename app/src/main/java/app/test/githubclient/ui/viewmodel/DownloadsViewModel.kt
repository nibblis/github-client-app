package app.test.githubclient.ui.viewmodel

import android.os.Environment
import app.test.githubclient.data.model.Download
import app.test.githubclient.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class DownloadsState(
    val downloads: List<Download> = emptyList(),
    val showProgressBar: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val messageState: String? = null,
)

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: GithubRepository,
) : BaseViewModel<DownloadsState>(DownloadsState()) {

    init {
        loadDownloads()
    }

    private fun loadDownloads() = launch {
        repository.getAllDownloads().collect { downloadsList ->
            _state.update { it.copy(downloads = downloadsList) }
        }
    }

    fun deleteDownload(download: Download) = launch {
        repository.deleteDownload(download)
        _state.update { it.copy(messageState = "Error deleting repository file") }
        try {

            val file = getFileName(download)

            if (file.exists()) {
                val isDeleted = file.delete()
                if (!isDeleted) {
                    _state.update { it.copy(messageState = "Failed to delete file: ${file.absolutePath}") }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(messageState = "Error deleting repository file $e") }
        }
    }

    private fun getFileName(download: Download): File {
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(downloadDir, "${download.username}_${download.repositoryName}.zip")
    }

    fun dissmissErrorMessage() {
        _state.update { it.copy(messageState = null) }
    }

    fun showDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun resetDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }
}