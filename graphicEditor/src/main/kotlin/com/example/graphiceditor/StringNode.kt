package com.example.graphiceditor

import com.example.graphiceditor.NodeTypes
import com.example.graphiceditor.ValueNode

class StringNode : ValueNode() {
    override val nodeType = NodeTypes.STRING
    override fun addInit() {
        valueField!!.text = ""
        titleBar!!.text = "[string]"

        valueField!!.textProperty().addListener { _, _, _ ->
            outputLink?.kickAction()
        }
    }

    override fun getValue(): String {
        return valueField!!.text
    }
}