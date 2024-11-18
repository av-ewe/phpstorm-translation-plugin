//
//import com.intellij.openapi.components.PersistentStateComponent
//import com.intellij.openapi.components.State
//import com.intellij.openapi.components.Storage
//import com.intellij.openapi.project.Project
//import com.intellij.util.xmlb.XmlSerializerUtil
//
//@State(name = "YourPluginSettings", storages = [Storage("LocalizationPluginSettings.xml")])
//class PluginSettings : PersistentStateComponent<PluginSettings> {
//    var option1: String? = null
//    var option2: Boolean = false
//
//    override fun getState(): PluginSettings {
//        return this
//    }
//
//    override fun loadState(state: PluginSettings) {
//        XmlSerializerUtil.copyBean(state, this)
//    }
//
//    companion object {
//        fun getInstance(project: Project): PluginSettings =
//            project.getService(PluginSettings::class.java)
//    }
//}