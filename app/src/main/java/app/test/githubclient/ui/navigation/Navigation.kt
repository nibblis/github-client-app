package app.test.githubclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.test.githubclient.ui.screens.DownloadsScreen
import app.test.githubclient.ui.screens.SearchScreen

@Composable
fun NavigationController() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Search
    ) {
        composable<Screens.Search> {
            SearchScreen(
                navigateToDownloads = {
                    navController.navigate(Screens.Downloads)
                }
            )
        }
        composable<Screens.Downloads> {
            DownloadsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}