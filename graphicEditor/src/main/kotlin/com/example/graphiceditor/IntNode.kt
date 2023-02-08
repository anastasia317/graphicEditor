package com.example.graphiceditor

import com.example.graphiceditor.NodeTypes
import com.example.graphiceditor.ValueNode

class IntNode : ValueNode() {
    override val nodeType = NodeTypes.INT

    override fun addInit() {
        valueField!!.text = "0"
        titleBar!!.text = "[int]"

        valueField!!.textProperty().addListener { _, _, _ ->
            outputLink?.kickAction()
        }
    }

    override fun getValue(): Int? {
        return valueField!!.text.toIntOrNull()
    }

}