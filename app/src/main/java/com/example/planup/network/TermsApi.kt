package com.example.planup.network

import com.example.planup.network.dto.term.TermsDetailResponse
import com.example.planup.network.dto.term.TermsListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TermsApi {
    // 약관 목록 조회
    @GET("/terms")
    suspend fun getTermsList(): Response<TermsListResponse>

    // 약관 상세 조회
    @GET("/terms/{termsId}")
    suspend fun getTermsDetail(
        @Path("termsId") termsId: Int
    ): Response<TermsDetailResponse>
}
