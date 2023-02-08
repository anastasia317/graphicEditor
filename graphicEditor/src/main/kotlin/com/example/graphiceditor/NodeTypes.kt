package com.example.graphiceditor

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import java.io.ByteArrayInputStream

enum class NodeTypes {
    INT, FLOAT, STRING, IMAGE, NONE
}

abstract class ValueNode : DraggableNode() {
    @FXML
    var valueField: TextField? = null

    init {
        init("ValueNode.fxml")
    }

    override fun updateNode() {}

    override fun toData(): NodeData {
        val data = super.toData()
        data.data = valueField!!.text
        return data
    }

    override fun fromData(nodeData: NodeData) {
        super.fromData(nodeData)
        valueField!!.text = nodeData.data
    }
}

abstract class ImageNode : DraggableNode() {
    override val nodeType = NodeTypes.IMAGE

    @FXML
    var firstLink: AnchorPane? = null

    @FXML
    var imageView: ImageView? = null
    override fun updateNode() {
        val img = getValue() as Mat?
        if (img != null) {
            val buff = MatOfByte()
            Imgcodecs.imencode(".png", img, buff)
            imageView!!.isVisible = true
            imageView!!.image = Image(ByteArrayInputStream(buff.toArray()))
        }
    }
}



