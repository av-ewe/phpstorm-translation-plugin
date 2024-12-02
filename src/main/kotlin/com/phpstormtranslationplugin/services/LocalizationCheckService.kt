package com.phpstormtranslationplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import fleet.util.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Service(Service.Level.PROJECT)
class LocalizationCheckService(
    private val project: Project,
    private val cs: CoroutineScope
) {
    init {
        this.cs.launch {  }
    }

    fun getRandomNumber(): Int {
        return Random.nextInt(100)
    }
}