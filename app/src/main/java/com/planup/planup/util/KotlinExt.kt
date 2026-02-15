package com.planup.planup.util

import kotlinx.coroutines.CancellationException

inline fun <R> safeRunCatching(block: () -> R): Result<R> = runCatching(block)
    .onFailure { if (it is CancellationException) throw it }