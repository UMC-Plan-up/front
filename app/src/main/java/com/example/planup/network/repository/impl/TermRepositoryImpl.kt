package com.example.planup.network.repository.impl

import com.example.planup.network.TermsApi
import com.example.planup.network.repository.TermRepository
import javax.inject.Inject
import javax.inject.Singleton

class TermRepositoryImpl @Inject constructor(
    private val termsApi: TermsApi
): TermRepository {
}