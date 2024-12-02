package com.phpstormtranslationplugin.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.phpstormtranslationplugin.services.LocalizationCheckService
import javax.swing.JButton


class PluginToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val pluginToolWindow = PluginToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(pluginToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class PluginToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<LocalizationCheckService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel("Lorem")
            add(label)
            add(JButton("Button").apply {
                addActionListener {
                    label.text = "${service.getRandomNumber()}"
                }
            })
        }
    }
}
