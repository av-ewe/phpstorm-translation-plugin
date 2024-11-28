package com.phpstormtranslationplugin

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JPanel

class TranslationPluginSettingsComponent {
    val panel: JPanel
    private val llPath = JBTextField()
    private val apiKeyDeepL = JBTextField()
    private val useDeepL = JBCheckBox(MyBundle.message("useDeepL"))

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Locallang files path: "), llPath, 1, true)
            .addSeparator(6)
            .addLabeledComponent(JBLabel("DeepL API-Key:"), apiKeyDeepL, 5, false)
            .addComponent(useDeepL, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    @get:NotNull

    var translationFilePath: String?
        get() = llPath.text
        set(newText) {
            llPath.text = newText
        }

    var apiKeyValueDeepL: String?
        get() = apiKeyDeepL.text
        set(newText) {
            apiKeyDeepL.text = newText
        }

    var useApiDeepL: Boolean
        get() = useDeepL.isSelected
        set(newStatus) {
            useDeepL.isSelected = newStatus
        }
}