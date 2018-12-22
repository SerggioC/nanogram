package com.sergiocruz.nanogram.model

data class RedirectResult(
    val code: String? = null,
    val error: String? = null,
    val errorReason: String? = null,
    val errorDescription: String? = null
)