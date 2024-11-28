package com.phpstormtranslationplugin


import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.swing.JComponent


internal class TranslationPluginSettingsConfigurable : Configurable {
    private var settingsComponent: TranslationPluginSettingsComponent? = null

    @Nullable
    override fun createComponent(): JComponent {
        settingsComponent = TranslationPluginSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val state: TranslationPluginSettings.State =
            Objects.requireNonNull(TranslationPluginSettings.instance.state)
        return  settingsComponent?.translationFilePath.equals(state.llFilePath) ||
                !settingsComponent?.apiKeyValueDeepL.equals(state.apiKeyDeppL) ||
                settingsComponent?.useApiDeepL !== state.useApiDeepL
    }

    override fun apply() {
        val state: TranslationPluginSettings.State =
            Objects.requireNonNull(TranslationPluginSettings.instance.state)
        state.llFilePath = settingsComponent?.translationFilePath ?: ""
        state.apiKeyDeppL = settingsComponent?.apiKeyValueDeepL ?: ""
        state.useApiDeepL = settingsComponent?.useApiDeepL ?: false
    }

    override fun reset() {
        val state: TranslationPluginSettings.State =
            Objects.requireNonNull(TranslationPluginSettings.instance.state)
        settingsComponent?.translationFilePath = state.llFilePath
        settingsComponent?.apiKeyValueDeepL = state.apiKeyDeppL
        settingsComponent?.useApiDeepL = state.useApiDeepL
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String {
        return "TYPO3 LLL Settings"
    }
}