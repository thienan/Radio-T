package pro.vylgin.radiot.model.data.server

import io.reactivex.Single
import pro.vylgin.radiot.entity.Entry
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RadioTApi {
    companion object {
        const val API_PATH = "site-api"
    }

    @GET("$API_PATH/last/{posts}")
    fun getEntries(
            @Path("posts") posts: Int?
    ): Single<List<Entry>>

    @GET("$API_PATH/last/{posts}")
    fun getEntries(
            @Path("posts") posts: Int?,
            @Query("categories") categories: String
    ): Single<List<Entry>>

    @GET("$API_PATH/podcast/{podcastNumber}")
    fun getEntry(
            @Path("podcastNumber") podcastNumber: Int
    ): Single<Entry>


}