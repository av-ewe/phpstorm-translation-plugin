import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

class ConfigureTranslationAction: AnAction() {
    override fun update(event: AnActionEvent) {
//        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
//        val primaryCaret = editor.caretModel.primaryCaret
//        event.presentation.isEnabledAndVisible = primaryCaret.hasSelection()
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(CommonDataKeys.PROJECT) ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val caret = editor.caretModel.primaryCaret
        val translationArguments = getTranslationArgumentsFromCaret(document, caret)
        println(translationArguments)

        ConfigureTranslationDialog(project, translationArguments).show()  //TODO: rename to Update... or Modify...
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    //TODO:selection direct to key?
    private fun getTranslationArgumentsFromCaret(document: Document, caret: Caret): Map<String, String> {
        val lineNumber: Int = caret.logicalPosition.line
        val lineContent = document.getText(
            TextRange(
                document.getLineStartOffset(lineNumber),
                document.getLineEndOffset(lineNumber)
            )
        )
        val transKeyResult = Regex("key ?[=:] ?[`\"']{1,3}(.*?)[`\"']{1,3}").find(lineContent)
        val transKey: String = transKeyResult?.groups?.get(1)?.value ?: ""

        val defaultValueResult = Regex("default ?[=:] ?[`\"']{1,3}(.*?)[`\"']{1,3}").find(lineContent)
            ?: Regex(">(.*?)</f:translate>").find(lineContent)
        val defaultValue: String = defaultValueResult?.groups?.get(1)?.value ?: ""

        return mapOf("transKey" to transKey, "defaultValue" to defaultValue)
    }
}