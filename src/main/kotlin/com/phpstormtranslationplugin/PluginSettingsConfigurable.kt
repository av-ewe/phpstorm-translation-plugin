package com.phpstormtranslationplugin


import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.swing.JComponent


internal class PluginSettingsConfigurable : Configurable {
    private var settingsComponent: PluginSettingsComponent? = null

    @Nullable
    override fun createComponent(): JComponent {
        settingsComponent = PluginSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val state: PluginSettings.State =
            Objects.requireNonNull(PluginSettings.instance.state)
        return  settingsComponent?.translationFilePath.equals(state.llFilePath) ||
                !settingsComponent?.apiKeyValueDeepL.equals(state.apiKeyDeppL) ||
                settingsComponent?.useApiDeepL !== state.useApiDeepL
    }

    override fun apply() {
        val state: PluginSettings.State =
            Objects.requireNonNull(PluginSettings.instance.state)
        state.llFilePath = settingsComponent?.translationFilePath ?: ""
        state.apiKeyDeppL = settingsComponent?.apiKeyValueDeepL ?: ""
        state.useApiDeepL = settingsComponent?.useApiDeepL ?: false
    }

    override fun reset() {
        val state: PluginSettings.State =
            Objects.requireNonNull(PluginSettings.instance.state)
        settingsComponent?.translationFilePath = state.llFilePath
        settingsComponent?.apiKeyValueDeepL = state.apiKeyDeppL
        settingsComponent?.useApiDeepL = state.useApiDeepL
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String {
        return PluginBundle.message("settings.display_name")
    }
}