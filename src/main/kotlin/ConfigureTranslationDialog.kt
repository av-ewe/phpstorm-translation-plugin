
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.setEmptyState
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.components.BorderLayoutPanel
import com.phpstormtranslationplugin.TranslationPluginIcons
import com.phpstormtranslationplugin.TranslationPluginSettings
import com.phpstormtranslationplugin.services.LocalizationService
import com.phpstormtranslationplugin.services.TranslationService
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ConfigureTranslationDialog(
    project: Project,
    translationArguments: Map<String, String>,
    ) : DialogWrapper(project, true) {

    private val pluginSettings = TranslationPluginSettings.instance
    private var translationKey = translationArguments["transKey"]?:""
    private val defaultValue = translationArguments["defaultValue"]
    private val tfKey = JTextField(translationKey)
    private val langPanel = JPanel(VerticalLayout(10))
    private val localizationService = LocalizationService(project)
    private val translationService = TranslationService()
    private val languageCodes = localizationService.getLanguageCodesFromFiles()
    private val translationFields = mutableMapOf<String, JTextField>()
    private val oldTranslationFields = mutableMapOf<String, String>()

    init {
        init()
        getButton(okAction)?.text = "save"
        getButton(okAction)?.addActionListener {
            languageCodes.forEach { key ->
                val textValue = translationFields[key]?.text
                if (textValue == oldTranslationFields[key] || textValue == null) {
                    return@forEach
                }
                if (key == "en") localizationService.updateSourceTranslations(translationKey, textValue, languageCodes)
                else localizationService.updateTranslation(translationKey, textValue, key)
            }
        }
        val tps = TranslationPluginSettings.instance
        println(tps.state.apiKeyDeppL)
        println(tps.state.useApiDeepL)
    }

    override fun getPreferredFocusedComponent(): JTextField? {
        return translationFields["en"]
    }

    //TODO: make scrollable!
    override fun createCenterPanel(): JComponent {

        val panel = JPanel(VerticalLayout(10))

        title = "Update Translation"
        tfKey.preferredSize = Dimension(320, 40)
        tfKey.document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent) { onTextChanged() }
            override fun removeUpdate(e: DocumentEvent) { onTextChanged() }
            override fun changedUpdate(e: DocumentEvent) { onTextChanged() }
        })

        panel.minimumSize = Dimension(160, 160) //min 100 100
        panel.add(JLabel("Translation key").apply {
            labelFor = tfKey
        })
        panel.add(tfKey)
        panel.add(JSeparator())

        langPanel.apply { languageCodes.forEach { key -> this.add(getLanguageField(key)) } }
        panel.add(langPanel)
        panel.add(JSeparator())

        val newLangBox = BorderLayoutPanel()
        val newLangTextField = JBTextField().setEmptyState("e.g. \"de\"")
        val newLangLabel = JLabel(" Add new language ")
        val newLangButton = JButton("add")

        newLangTextField.columns = 8
        newLangTextField.horizontalAlignment = SwingConstants.LEADING
        newLangLabel.horizontalAlignment = SwingConstants.LEADING
        newLangLabel.horizontalTextPosition = SwingConstants.TRAILING
        newLangButton.preferredSize = Dimension(40,-1)
        newLangButton.horizontalAlignment = SwingConstants.CENTER
        newLangButton.horizontalTextPosition = SwingConstants.TRAILING
        newLangButton.addActionListener {
            val newLangCode = newLangTextField.text
            if (newLangCode.isNullOrEmpty() || languageCodes.contains(newLangCode.lowercase())) {
                return@addActionListener
            }
            newLangCode.lowercase()
            langPanel.add(getLanguageField(newLangCode))
            languageCodes.add(newLangCode)
            newLangTextField.text = null
            if (this.window.height > 1000) return@addActionListener
            val newHeight = this.window.height + 50
            this.window.size = Dimension(this.window.width, newHeight)
            this.window.revalidate()
        }

        newLangBox.addToLeft(newLangTextField)
        newLangBox.addToCenter(newLangLabel)
        newLangBox.addToRight(newLangButton)
        panel.add(newLangBox)
        panel.add(JSeparator())

        return panel
    }

    private fun getLanguageField(langCode: String): BorderLayoutPanel {
        val translations = localizationService.getAllTranslationsByKey(translationKey)
        val layout = BorderLayoutPanel()
        val label = JLabel(langCode)
        val textFieldValue = translations[langCode]
            ?: if (langCode == "en") defaultValue ?: translationKey.split (".").last() else ""
        val textField = JBTextField(textFieldValue).setEmptyState("No translation found")
        val transBtn = JButton(TranslationPluginIcons.DeepL)

        label.horizontalAlignment = SwingConstants.LEADING
        label.labelFor = textField
        label.preferredSize = Dimension(24,-1)

        textField.horizontalAlignment = SwingConstants.LEADING

        if (pluginSettings.state.useApiDeepL) {
            transBtn.horizontalAlignment = SwingConstants.CENTER
            transBtn.horizontalTextPosition = SwingConstants.TRAILING
            if (langCode != "en") {
                transBtn.toolTipText = "Get a translation with DeepL of the \"en\" field value"
                transBtn.addActionListener {
                    val sourceValue = getTranslationBySourceValue(langCode)
                    textField.text = sourceValue
                    textField.repaint()
                }
                transBtn.preferredSize = Dimension(35,-1)
            } else {
                transBtn.text = "all"
                transBtn.toolTipText = "Get translations with DeepL of the \"en\" field value for every language below"
                transBtn.preferredSize = Dimension(55,-1)
                transBtn.addActionListener {
                    translationFields.forEach { (langCode, field) ->
                        if (langCode == "en") return@forEach
                        val sourceValue = getTranslationBySourceValue(langCode)
                        field.text = sourceValue
                        field.repaint()
                    }
                }
            }
            layout.addToRight(transBtn)
        }

        layout.addToLeft(label)
        layout.addToCenter(textField)

        translationFields[langCode] = textField
        oldTranslationFields[langCode] = textField.text ?: ""

        return layout
    }

    private fun getTranslationBySourceValue(langCode: String): String? {
        val sourceValue = translationFields["en"]?.text ?: return null
        return translationService.translateValueWithDeepL(sourceValue, langCode)
    }

    private fun onTextChanged() {
        translationKey = tfKey.text
        langPanel.components.forEach { component ->
            langPanel.remove(component)
        }
        langPanel.apply { languageCodes.forEach { key -> this.add(getLanguageField(key)) } }
        langPanel.revalidate()
    }
}