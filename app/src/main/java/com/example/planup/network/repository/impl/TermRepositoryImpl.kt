package com.example.planup.network.repository.impl

import com.example.planup.network.ApiResult
import com.example.planup.network.TermsApi
import com.example.planup.network.dto.term.TermDetail
import com.example.planup.network.dto.term.TermItem
import com.example.planup.network.repository.TermRepository
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TermRepositoryImpl @Inject constructor(
    private val termsApi: TermsApi
) : TermRepository {

    override suspend fun getTerms(): ApiResult<List<TermItem>> = withContext(Dispatchers.IO) {
        safeResult(
            response = { termsApi.getTermsList() },
            onResponse = {
                if (it.isSuccess) {
                    ApiResult.Success(it.result)
                } else {
                    ApiResult.Fail(it.message)
                }
            }
        )
    }

    override suspend fun getTermsDetail(termsId: Int): ApiResult<TermDetail> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = { termsApi.getTermsDetail(termsId) },
                onResponse = {
                    if (it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }
}