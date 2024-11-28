package com.phpstormtranslationplugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull

@State(name = "com.phpstormtranslationplugin.TranslationPluginSettings", storages = [Storage("SdkSettingsPlugin.xml")])
internal class TranslationPluginSettings

    : PersistentStateComponent<TranslationPluginSettings.State> {
    internal class State {
        var llFilePath: @NonNls String = "/packages/av_site/Resources/Private/Language"
        var apiKeyDeppL: @NonNls String = ""
        var useApiDeepL: Boolean = false
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(@NotNull state: State) {
        myState = state
    }

    companion object {
        val instance: TranslationPluginSettings
            get() = ApplicationManager.getApplication()
                .getService(TranslationPluginSettings::class.java)
    }
}