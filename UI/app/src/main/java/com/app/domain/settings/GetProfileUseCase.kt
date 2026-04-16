package com.app.domain.settings

import com.app.AppGraph
import com.app.data.repository.SettingsRepository

class GetProfileUseCase(
    private val repository: SettingsRepository = AppGraph.settingsRepository,
) {
    suspend operator fun invoke() = repository.getProfile()
}
