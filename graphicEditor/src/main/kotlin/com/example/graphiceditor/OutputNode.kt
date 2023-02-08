package com.example.graphiceditor

import com.example.graphiceditor.ImageNode
import com.example.graphiceditor.NodeTypes
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.FileChooser
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.IOException

class OutputNode : ImageNode() {
    @FXML
    var saveButton: Button? = null

    override fun getValue(): Mat? {
        return nodes["firstLink"]!!.second?.getValue() as Mat? ?: return null
    }

    override fun addInit() {
        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        saveButton!!.onAction = EventHandler {
            val mat = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return@EventHandler

            val fileChooser = FileChooser()
            fileChooser.title = "Save Image"
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("*.png", "*.png"),
                FileChooser.ExtensionFilter("*.jpg", "*.jpg"))
            val dir = fileChooser.showSaveDialog(scene.window)
            if (dir != null) {
                try {
                    Imgcodecs.imwrite(dir.absolutePath, mat)
                } catch (e: IOException) {
                    println(e)
                }
            }
        }
    }

    init {
        init("OutputNode.fxml")
    }
}