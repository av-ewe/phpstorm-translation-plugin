package com.phpstormtranslationplugin.xlf

import com.intellij.util.xml.DomElement

interface RootTag: DomElement {
    fun getFileTag(): FileTag
}
interface FileTag: DomElement {
    fun getBodyTag(): BodyTag
}
interface BodyTag: DomElement
