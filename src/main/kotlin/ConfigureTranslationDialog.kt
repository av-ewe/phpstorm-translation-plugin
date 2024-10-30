import com.github.avewe.phpstormtranslationplugin.services.LocalizationService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.panels.HorizontalBox
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.Dimension
import javax.swing.*

class ConfigureTranslationDialog(
    project: Project,
    private val selection: String,

) : DialogWrapper(project, true) {

    private val tfFile = JTextField("")
    private val tfKey = JTextField(selection.ifEmpty { "" })

    init {
        init()

        getButton(okAction)?.addActionListener {
            val localizationService = LocalizationService(project)
            val path = tfFile.text.ifEmpty { "${project.basePath}/packages/av_site/Resources/Private/Language" }
            localizationService.searchLanguageFilesInDirectory(path)
            println("${project.basePath}/packages/av_site/Resources/Private/Language")
        }
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return tfKey
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(VerticalLayout(10))

        tfFile.preferredSize = Dimension(320, 40)
        tfKey.preferredSize = Dimension(320, 40)

        panel.add(JLabel("Base file").apply {
            labelFor = tfFile
        })
        panel.add(tfFile)
        panel.add(JSeparator())
        panel.add(JLabel("Translation key").apply {
            labelFor = tfKey
        })
        panel.add(tfKey)
        panel.add(JSeparator())

        arrayOf("en", "de").forEach { key ->
            val layout = HorizontalBox()
            val jLabel = JLabel(key)
            val value = JTextField(if (key == "en") selection.split(".").last() else "")
            jLabel.labelFor = value
            jLabel.preferredSize = Dimension(40, 20)
            value.preferredSize = Dimension(280, 40)
            layout.add(jLabel)
            layout.add(value)
            panel.add(layout)
        }

        return panel
    }
}