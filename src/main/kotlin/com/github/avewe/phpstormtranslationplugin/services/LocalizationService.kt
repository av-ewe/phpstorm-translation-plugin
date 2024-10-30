package com.github.avewe.phpstormtranslationplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.avewe.phpstormtranslationplugin.MyBundle
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

@Service(Service.Level.PROJECT)
class LocalizationService(private val project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
    }

    fun searchLanguageFilesInDirectory(directoryPath: String) {
        val virtualFileSystem = LocalFileSystem.getInstance()
        val directory = virtualFileSystem.findFileByPath(directoryPath)

        if (directory != null && directory.isDirectory) {
            processDirectory(directory)
        } else {
            println("Verzeichnis nicht gefunden oder ist keine Verzeichnis: $directoryPath")
        }
    }

    private fun processDirectory(directory: VirtualFile) {
        for (file in directory.children) {
            if (file.isDirectory) {
                println("Unterverzeichnis gefunden: ${file.path}")
//                processDirectory(file) // Rekursiv für Unterverzeichnisse
            } else {
                println("Datei gefunden: ${file.path}")
            }
        }
    }

    fun findLocallangFiles() {
        val fileNamePattern = Regex("[a-z]{2,3}.locallang.xlf")
        val searchScope = ""
    }
}
