package com.example.graphiceditor

import com.example.graphiceditor.NodeTypes
import com.example.graphiceditor.ValueNode

class FloatNode : ValueNode() {

    override val nodeType = NodeTypes.FLOAT

    override fun addInit() {
        valueField!!.text = "0.0"
        titleBar!!.text = "[float]"

        valueField!!.textProperty().addListener { _, _, _ ->
            outputLink?.kickAction()
        }
    }

    override fun getValue(): Float? {
        return valueField!!.text.toFloatOrNull()
    }
}