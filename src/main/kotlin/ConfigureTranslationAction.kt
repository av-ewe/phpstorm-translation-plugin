import com.github.avewe.phpstormtranslationplugin.services.LocalizationService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class ConfigureTranslationAction: AnAction() {
    override fun update(event: AnActionEvent) {
//        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
//        val primaryCaret = editor.caretModel.primaryCaret
//        event.presentation.isEnabledAndVisible = primaryCaret.hasSelection()
    }

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val primaryCaret = editor.caretModel.primaryCaret
        val selection = primaryCaret.selectedText.toString()

        val project = event.getData(CommonDataKeys.PROJECT) ?: return
        ConfigureTranslationDialog(project, selection).show()

        println(event.getData(CommonDataKeys.EDITOR))
        println(selection)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}