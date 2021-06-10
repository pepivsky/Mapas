package com.pepivsky.mapas.service


import com.pepivsky.mapas.response.GetNotariesResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {

    @POST("notaries/all")
    @FormUrlEncoded
    fun getNotaries(
        @Header("Authorization") token: String,
        @Field("user_id") id: String,
        @Field("lat") lat: String,
        @Field("lon") long: String
    ): Call<GetNotariesResponse>

}