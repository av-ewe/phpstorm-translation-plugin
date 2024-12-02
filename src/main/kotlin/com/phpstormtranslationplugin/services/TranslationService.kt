package com.phpstormtranslationplugin.services

import com.deepl.api.Translator
import com.intellij.openapi.components.Service
import com.phpstormtranslationplugin.PluginSettings

@Service(Service.Level.PROJECT)
class TranslationService {
    private val pluginSettings = PluginSettings.instance
    private val apiKeyDeepL = pluginSettings.state.apiKeyDeppL

    fun translateValue(text: String, langCode: String): String {
        return "$text [${langCode.uppercase()}]"
    }

    fun translateValueWithDeepL(text: String, langCode: String): String {
        val translator = Translator(apiKeyDeepL)
        val translatedText = translator.translateText(text, "EN", langCode.uppercase())
        return translatedText.text
    }
}