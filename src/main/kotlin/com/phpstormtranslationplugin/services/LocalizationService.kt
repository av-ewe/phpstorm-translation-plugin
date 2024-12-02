package com.phpstormtranslationplugin.services

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.phpstormtranslationplugin.PluginSettings

@Service(Service.Level.PROJECT)
class LocalizationService(private val project: Project) {

    private val pluginSettings = PluginSettings.instance
    private val languageDirectoryPath = project.basePath + pluginSettings.state.llFilePath

    fun getLanguageCodesByFiles(): MutableSet<String> {
        val languageCodes = mutableSetOf<String>()
        val languageDirectory = getLanguageDirectory() ?: return languageCodes
        val searchPattern = Regex("[a-z]{2,3}\\.locallang(_be)?(_form)?\\.xlf")

        languageCodes.add("en")
        for (file in languageDirectory.children) {
            if (searchPattern.containsMatchIn(file.name)) {
                languageCodes.add(file.name.split(".")[0])
            }
        }
        return languageCodes
    }

    fun getTranslationsByKey(
        translationKey: String
    ): MutableMap<String, String> {
        val translations = mutableMapOf<String, String>()
        val searchPattern = "^([a-z]{2,3}\\.)?locallang.xlf"
        val foundFiles = findTranslationFiles(searchPattern) ?: return translations

        for (file in foundFiles) {
            var langCode = file.name.split(".")[0]
            if (Regex("locallang").containsMatchIn(langCode)) langCode = "en"
            val translationValue = getTranslationFromXlfFile(file, translationKey, (langCode == "en")) ?: continue

            translations[langCode] = translationValue
        }
        return translations
    }

    fun updateTranslation(transKey: String, newValue: String, langCode: String, updateSource: Boolean = false) {
        val languageDirectory = getLanguageDirectory() ?: return
        val filePrefix = if(langCode == "en") "" else "$langCode."
        val foundFiles = findTranslationFiles("^${filePrefix}locallang.xlf")
        println("foundFiles: $foundFiles")

        if (foundFiles == null && !updateSource) {
            println("createFile -> updateSource: false")
            createTranslationFile(languageDirectory, langCode, transKey, newValue)
            return
        }
        for (file in foundFiles ?: return) {
            println("fileFound ->updateSource: $updateSource")
            setTranslationInXmlFile(newValue, file, transKey, langCode, updateSource)
        }
    }

    fun updateSourceTranslations(transKey: String, newValue: String, langCodes: MutableSet<String>) {
        langCodes.forEach { langCode ->
            updateTranslation(transKey, newValue, langCode, true)
        }
    }

    private fun getTranslationFromXlfFile(xlfFile: XmlFile, transKey: String, isBaseFile: Boolean = false): String? {
        var targetValue: String? = null
        val xmlBodyTag = getXlfBodyTag(xlfFile) ?: return null
        val transTags = xmlBodyTag.findSubTags("trans-unit")

        for (tag in transTags) {
            if (tag.getAttribute("id") == null || tag.getAttribute("id")?.value != transKey) continue
            targetValue = if (isBaseFile) {
                tag.findFirstSubTag("source")?.value?.text
            } else {
                tag.findFirstSubTag("target")?.value?.text
            }
            break
        }
        return targetValue
    }

    private fun getLanguageDirectory(): VirtualFile? {
        val virtualFileSystem = LocalFileSystem.getInstance()
        val directory = virtualFileSystem.findFileByPath(languageDirectoryPath)
        if (directory == null || !directory.isDirectory) {
            return null
        }
        return directory
    }

    private fun getXlfBodyTag(xlfFile: XmlFile): XmlTag? {
        val rootTag: XmlTag? = xlfFile.rootTag
        val fileTag = rootTag?.findFirstSubTag("file")
        return fileTag?.findFirstSubTag("body")
    }

    private fun getSourceTranslation(transKey: String): String? {
        val foundFiles = findTranslationFiles("^locallang.xlf") ?: return null
        return getTranslationFromXlfFile(foundFiles[0], transKey, true)
    }

    private fun findTranslationFiles(searchPattern: String): List<XmlFile>? {
        val files = mutableListOf<XmlFile>()
        val languageDirectory = getLanguageDirectory() ?: return files
        val pattern = Regex(searchPattern)

        for (file in languageDirectory.children) {
            if (!pattern.containsMatchIn(file.name)) continue
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile !is XmlFile) continue
            files.add(psiFile)
        }
        return files.ifEmpty { null }
    }

    private fun setTranslationInXmlFile(
        newValue: String,
        xlfFile: XmlFile,
        transKey: String,
        langCode: String,
        updateSource: Boolean = false,
    ) {
        val xlfBodyTag = getXlfBodyTag(xlfFile) ?: return
        val xlfTransUnitTags = xlfBodyTag.findSubTags("trans-unit")
        val tag = xlfTransUnitTags.find { xmlTag: XmlTag? ->  xmlTag?.getAttribute("id")?.value == transKey }

        WriteCommandAction.runWriteCommandAction(project) {
            if (tag == null) {
                val newTag = createTranslationTag(xlfBodyTag, transKey, newValue, langCode, updateSource)
                xlfBodyTag.addSubTag(newTag, false)
                return@runWriteCommandAction
            }
            if (updateSource) {
                tag.findFirstSubTag("source")?.value?.text = newValue
                return@runWriteCommandAction
            }
            tag.findFirstSubTag("target")?.value?.text = newValue
            tag.findFirstSubTag("target")?.setAttribute("state", "translated")
        }
    }

    private fun createTranslationTag(
        parentTag: XmlTag,
        transKey: String,
        value: String,
        langCode: String,
        updateSource: Boolean = false
    ): XmlTag {
        val sourceTranslation = getSourceTranslation(transKey)
        val newTransTag = parentTag.createChildTag("trans-unit", parentTag.namespace, null, false)
        newTransTag.setAttribute("id", transKey)
        val newSourceTag = newTransTag.createChildTag(
            "source",
            newTransTag.namespace,
            if (updateSource) value else sourceTranslation,
            false
        )
        val newTargetTag = newTransTag.createChildTag(
            "target",
            newTransTag.namespace,
            (if (!updateSource) value else null),
            false
        )
        //TODO translation-state for missing translation
        if (!updateSource) newTargetTag.setAttribute("state", "translated")
        newTransTag.addSubTag(newSourceTag, false)
        if (langCode !== "en") newTransTag.addSubTag(newTargetTag, false)
        return newTransTag
    }

    private fun createTranslationFile(
        directory: VirtualFile,
        langCode: String,
        transKey: String? = null,
        value: String? = null,
        updateSource: Boolean = false
    ) {
        val sourceTranslation = transKey?.let { getSourceTranslation(it) }
        WriteCommandAction.runWriteCommandAction(project) {
            val newFile = directory.createChildData(this, "${langCode}.locallang.xlf")
            val newXmlFile = PsiManager.getInstance(project).findFile(newFile)
            newXmlFile?.fileDocument?.setText("""
                <?xml version="1.0" encoding="UTF-8"?>
                <xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
                    <file source-language="en" target-language="$langCode" datatype="plaintext" original="messages" date="2014-07-11T10:45:00Z" product-name="av_site">
                        <header/>
                        <body> ${if (transKey != null) { """ 
                            <trans-unit id="$transKey">
                                <source>${if (updateSource) value else sourceTranslation}</source>
                                <target state="translated">${if (!updateSource) value else ""}</target>
                            </trans-unit>""" } else {null}}
                        </body>
                    </file>
                </xliff>
            """.trimIndent())
        }
    }
}
