package com.sample.noeventbus.data.repository

import com.sample.noeventbus.data.local.ThemeDataSource
import com.sample.noeventbus.domain.model.AppTheme
import com.sample.noeventbus.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val themeDataSource: ThemeDataSource
) : ThemeRepository {

    override val theme: Flow<AppTheme> = themeDataSource.theme

    override suspend fun toggleTheme() {
        val currentTheme = theme.first()
        themeDataSource.saveTheme(currentTheme == AppTheme.Light)
    }
}
