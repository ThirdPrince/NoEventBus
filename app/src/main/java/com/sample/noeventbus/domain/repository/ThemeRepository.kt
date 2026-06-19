package com.sample.noeventbus.domain.repository

import com.sample.noeventbus.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val theme: Flow<AppTheme>
    suspend fun toggleTheme()
}
