package com.example.planup.network.repository

import com.example.planup.network.ApiResult
import com.example.planup.network.dto.term.TermDetail
import com.example.planup.network.dto.term.TermItem

interface TermRepository {
    suspend fun getTerms(): ApiResult<List<TermItem>>
    suspend fun getTermsDetail(termsId: Int): ApiResult<TermDetail>
}