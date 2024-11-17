package app.test.githubclient.data.api

import app.test.githubclient.data.model.Repository
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface GithubApi {
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String
    ): List<Repository>

    @GET("repos/{owner}/{repo}/zipball")
    @Streaming
    suspend fun downloadRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ResponseBody
}