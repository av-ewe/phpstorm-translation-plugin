package com.phpstormtranslationplugin

import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomElement

interface RootTag: DomElement {
    fun getFileTag(): FileTag
}
interface FileTag: DomElement {
    fun getBodyTag(): BodyTag
}
interface BodyTag: DomElement {
    fun getTransUnitTags(): List<XmlTag>
}
