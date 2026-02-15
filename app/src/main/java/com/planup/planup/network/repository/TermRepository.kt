package com.planup.planup.network.repository

import com.planup.planup.network.ApiResult
import com.planup.planup.network.dto.term.TermDetail
import com.planup.planup.network.dto.term.TermItem

interface TermRepository {
    suspend fun getTerms(): ApiResult<List<TermItem>>
    suspend fun getTermsDetail(termsId: Int): ApiResult<TermDetail>
}