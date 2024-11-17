package app.test.githubclient.data.interceptor

import app.test.githubclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class GitHubAuthInterceptor : Interceptor {

    private fun isValidToken(token: String): Boolean =
        token != "null" && token.isNotEmpty()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val modifiedRequest = if (isValidToken(BuildConfig.GITHUB_TOKEN)) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.GITHUB_TOKEN}")
                .build()
        } else {
            request
        }

        return chain.proceed(modifiedRequest)
    }
}