package app.test.githubclient.ui.viewmodel

import app.test.githubclient.data.model.Repository
import app.test.githubclient.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val repositories: List<Repository> = emptyList(),
    val showProgressBar: Boolean = false,
    val showAlreadyDownloadedDialog: Boolean = false,
    val showNotFoundLabel: Boolean = false,
    val messageState: String? = null,
    val searchQuery: String = "",
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository,
) : BaseViewModel<SearchState>(SearchState()) {

    private val searchQuery = MutableStateFlow("")

    init {
        searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                searchRepositories(query)
            }
            .launchIn(this)
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchQuery.value = query
    }

    private suspend fun searchRepositories(username: String) {

        if (username.isEmpty()) {
            _state.update { it.copy(repositories = emptyList()) }
            return
        }

        _state.update {
            it.copy(
                showProgressBar = true,
                repositories = emptyList(),
                showNotFoundLabel = false
            )
        }

        try {
            delay(300)
            repository.searchRepositories(username)
                .onSuccess { repos ->
                    _state.update {
                        it.copy(
                            repositories = repos,
                            messageState = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { it.copy(repositories = emptyList(), showNotFoundLabel = true) }
                    println("Error: ${exception.message}")
                }
        } finally {
            _state.update { it.copy(showProgressBar = false) }
        }
    }

    fun handleDownload(repo: Repository) = launch {
        if (repository.isRepositoryDownloaded(repo)) {
            _state.update { it.copy(showAlreadyDownloadedDialog = true) }
        } else {
            downloadRepository(repo)
        }
    }

    fun dissmissAlreadyDownloadedDialog() {
        _state.update { it.copy(showAlreadyDownloadedDialog = false) }
    }

    fun dissmissErrorMessage() {
        _state.update { it.copy(messageState = null) }
    }

    suspend private fun downloadRepository(repo: Repository) {
        _state.update { it.copy(showProgressBar = true) }
        try {
            repository.downloadRepository(repo.owner.login, repo.name)
        } catch (e: Exception) {
            _state.update { it.copy(messageState = "Failed to download repository: ${e.message}") }
        } finally {
            _state.update { it.copy(showProgressBar = false) }
        }
    }
}