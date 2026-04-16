package com.app.domain.settings

import com.app.AppGraph
import com.app.data.repository.SettingsRepository

class UpdateSecurityUseCase(
    private val repository: SettingsRepository = AppGraph.settingsRepository,
) {
    suspend operator fun invoke(key: String, enabled: Boolean) = repository.updateSecurity(key, enabled)
}
