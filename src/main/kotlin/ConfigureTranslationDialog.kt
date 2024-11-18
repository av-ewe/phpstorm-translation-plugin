import com.github.avewe.phpstormtranslationplugin.services.LocalizationService
import com.github.avewe.phpstormtranslationplugin.services.TranslationService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.setEmptyState
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.HorizontalBox
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ConfigureTranslationDialog(
    project: Project,
    translationArguments: Map<String, String>,
    ) : DialogWrapper(project, true) {

    private var translationKey = translationArguments["transKey"]?:""
    private val defaultValue = translationArguments["defaultValue"]
    private val tfKey = JTextField(translationKey)  //TODO: make translation key changeable in dialog
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
                println("saving to files -> language: $key, value: $textValue")
                if (key == "en") localizationService.updateSourceTranslations(translationKey, textValue, languageCodes)
                else localizationService.updateTranslation(translationKey, textValue, key)
            }
        }
    }

    override fun getPreferredFocusedComponent(): JTextField? {
        return translationFields["en"]
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(VerticalLayout(10))

        title = "Update Translation"
        tfKey.preferredSize = Dimension(320, 40)
        tfKey.document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent) { onTextChanged() }
            override fun removeUpdate(e: DocumentEvent) { onTextChanged() }
            override fun changedUpdate(e: DocumentEvent) { onTextChanged() }
        })

        panel.add(JLabel("Translation key").apply {
            labelFor = tfKey
        })
        panel.add(tfKey)
        panel.add(JSeparator())

        langPanel.apply { languageCodes.forEach { key -> this.add(getLanguageField(key)) } }
        panel.add(langPanel)
        panel.add(JSeparator())

        val boxAddLang = HorizontalBox()
        val tfAddLang = JBTextField().setEmptyState("Add language (e.g. \"de\")")
        val btnAddLang = JButton("add")
        val verticalSeparator = JSeparator()
        verticalSeparator.orientation = SwingConstants.VERTICAL
        verticalSeparator.preferredSize = Dimension(40, 40)
        val translateAllBtn = JButton("all", TranslationPluginIcons.DeepL)    //AllIcons.General.LocalizationSettings
        tfAddLang.preferredSize = Dimension(200, 40)

        btnAddLang.maximumSize = Dimension(20,40)
        btnAddLang.addActionListener {
            val newLangCode = tfAddLang.text ?: return@addActionListener
            newLangCode.lowercase()         //TODO: check if lang already exists
            langPanel.add(getLanguageField(newLangCode))
            languageCodes.add(newLangCode)
            tfAddLang.text = null
            langPanel.revalidate()
            println("Success?")
        }

        boxAddLang.add(tfAddLang)
        boxAddLang.add(btnAddLang)
        boxAddLang.add(verticalSeparator)
        panel.add(boxAddLang)
        panel.add(JSeparator())

        return panel
    }

    private fun getLanguageField(langCode: String): HorizontalBox {
        val translations = localizationService.getAllTranslationsByKey(translationKey)

        val layout = HorizontalBox() //HorizontalLayout(10)

        val label = JLabel(langCode)
        val textFieldValue = translations[langCode]
            ?: if (langCode == "en") defaultValue ?: translationKey.split (".").last() else ""
        val textField = JBTextField(textFieldValue).setEmptyState("No translation found")
        val transBtn = JButton(TranslationPluginIcons.DeepL) //AllIcons.General.LocalizationSettings
        label.labelFor = textField
        label.preferredSize = Dimension(16,-1)
//        textField.minimumSize = Dimension(100, 40)
//        textField.preferredSize = Dimension(304, 40)
//        textField.
        if (langCode != "en") {
            transBtn.addActionListener {
                val sourceValue = getTranslationFromSourceValue(langCode)
                textField.text = sourceValue
                textField.repaint()
            }
            transBtn.preferredSize = Dimension(40,-1)
        } else {
            transBtn.text = "all"
            transBtn.preferredSize = Dimension(60,-1)
            transBtn.addActionListener {
                translationFields.forEach { (key, field) ->
                    if (key == "en") return@forEach
                    val sourceValue = getTranslationFromSourceValue(key)
                    field.text = sourceValue
                    field.repaint()
                }
            }
        }
//        layout.preferredSize = Dimension(320, 40)

        layout.add(label)
        layout.add(textField)
        layout.add(transBtn)
//        layout.addLayoutComponent(label, HorizontalLayout.LEFT)
//        layout.addLayoutComponent(textField, HorizontalLayout.FILL)
//        layout.addLayoutComponent(transBtn, HorizontalLayout.LEFT)

        translationFields[langCode] = textField
        oldTranslationFields[langCode] = textField.text ?: ""

        return layout   //????
    }

    private fun getTranslationFromSourceValue(langCode: String): String? {
        val sourceValue = translationFields["en"]?.text ?: return null
        return translationService.translateValue(sourceValue, langCode)
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