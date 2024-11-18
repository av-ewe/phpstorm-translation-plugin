package com.github.avewe.phpstormtranslationplugin.services

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class TranslationService {

    fun translateValue(value: String, langCode: String): String {
        return "$value [${langCode.uppercase()}]"
    }
}