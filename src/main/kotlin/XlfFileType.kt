import com.intellij.icons.AllIcons
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class  XlfFileType : LanguageFileType(XMLLanguage.INSTANCE) {
    override fun getName(): String = "XML File"
    override fun getDescription(): String = "XLF file type"
    override fun getDefaultExtension(): String = "xlf"
    override fun getIcon(): Icon = AllIcons.Actions.InlayGlobe
}
