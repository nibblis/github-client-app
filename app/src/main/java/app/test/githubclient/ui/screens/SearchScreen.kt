package app.test.githubclient.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.test.githubclient.R
import app.test.githubclient.ui.components.RepositoryItem
import app.test.githubclient.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navigateToDownloads: () -> Unit,
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.messageState) {
        if (state.messageState != null) {
            snackbarHostState.showSnackbar(state.messageState!!)
            viewModel.dissmissErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Repositories") },
                actions = {
                    IconButton(onClick = navigateToDownloads) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_downloads),
                            contentDescription = "Downloads",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    Snackbar(snackbarData = it)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    label = { Text("Enter username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    singleLine = true
                )

                LazyColumn {
                    items(state.repositories) { repo ->
                        RepositoryItem(
                            repository = repo,
                            onDownloadClick = {
                                viewModel.handleDownload(repo)
                            },
                            onOpenInBrowser = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            if (state.showNotFoundLabel) {
                Text(
                    text = "No repositories found for the given username",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center))
            }

            if (state.showAlreadyDownloadedDialog) {
                ShowAlreadyDownloadedDialog(viewModel, navigateToDownloads)
            }

            if (state.showProgressBar) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ShowAlreadyDownloadedDialog(
    viewModel: SearchViewModel,
    navigateToDownloads: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = { viewModel.dissmissAlreadyDownloadedDialog() },
        title = { Text("Repository Already Downloaded") },
        text = { Text("This repository has already been downloaded. Would you like to go to Downloads?") },
        confirmButton = {
            TextButton(
                onClick = {
                    navigateToDownloads()
                    viewModel.dissmissAlreadyDownloadedDialog()
                }
            ) {
                Text("Go to Downloads")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.dissmissAlreadyDownloadedDialog() }) {
                Text("Cancel")
            }
        }
    )
}