package app.test.githubclient.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.test.githubclient.data.model.Download
import app.test.githubclient.ui.viewmodel.DownloadsViewModel
import app.test.githubclient.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    viewModel: DownloadsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
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
                title = { Text("Downloads") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(state.downloads) { download ->
                    ListItem(
                        modifier = Modifier.height(84.dp),
                        headlineContent = { Text(download.repositoryName) },
                        supportingContent = {
                            Column {
                                Text(download.username)
                                Text(
                                    text = "Downloaded: ${formatDate(download.timestamp)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null
                                )
                            }
                        },
                        trailingContent = {
                            IconButton(onClick = { viewModel.showDeleteDialog() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    )
                    if (state.showDeleteDialog) {
                        ShowDeleteDialog(viewModel, download)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowDeleteDialog(
    viewModel: DownloadsViewModel,
    download: Download,
) {
    AlertDialog(
        onDismissRequest = { viewModel.resetDeleteDialog() },
        title = { Text("Delete Repository") },
        text = { Text("Are you sure you want to delete ${download.repositoryName}?") },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteDownload(download)
                    viewModel.resetDeleteDialog()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.resetDeleteDialog() }) {
                Text("Cancel")
            }
        }
    )
}