package app.test.githubclient.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object Search : Screens()

    @Serializable
    data object Downloads : Screens()
}