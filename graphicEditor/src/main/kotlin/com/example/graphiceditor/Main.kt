package com.example.graphiceditor

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Screen
import javafx.stage.Stage
import org.opencv.core.Core

class Builder {
    private var root = AnchorPane()
    private val width = 1300.0
    private val height = 700.0
    private var scene = Scene(root, width, height)
    private var primaryScreenBounds = Screen.getPrimary().visualBounds

    fun start(): Scene {
        root.style = "-fx-background-color: #23232a;"
        root.children.add(addButtons())
        root.children.addAll(setInputNode(), setOutputNode())
        return scene
    }

    private fun setInputNode(): InputNode {

        val inputNode = InputNode()
        val deleteButton = inputNode.lookup("#deleteButton")
        deleteButton.isVisible = false
        inputNode.layoutX = 50.0
        inputNode.layoutY = 100.0
        inputNode.style = "-fx-border-radius: 0; -fx-border-width: 1; -fx-border-color: #c5c5c5; -fx-background-color: #323232;"

        return inputNode
    }

    private fun setOutputNode(): OutputNode {

        val outputNode = OutputNode()
        outputNode.layoutX = primaryScreenBounds.width - 300.0
        outputNode.layoutY = primaryScreenBounds.height / 2

        outputNode.style = "-fx-border-radius: 0; -fx-border-width: 1; -fx-border-color: #c5c5c5; -fx-background-color: #323232;"

        return outputNode
    }

    private fun addButtons(): HBox {
        val hbox = HBox()
        hbox.layoutX = 0.0
        hbox.layoutY = 0.0
        hbox.style = "-fx-background-color: #294a7a"
        hbox.prefWidth = primaryScreenBounds.width
        hbox.prefHeight = 26.0

        val buttonTypes = arrayOf("[open]", "[float]", "[int]", "[string]", "[add text]", "[add image]", "[grey]", "[brightness]", "[sepia]", "[invert]", "[blur]", "[move]", "[scale]", "[rotate]")

        for (buttonType in buttonTypes) {
            val button = Button(buttonType)
            button.style = "-fx-font-family: 'MS Gothic'; -fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-background-color: #294a7a; -fx-border-radius: 0;"

            button.onMouseEntered = EventHandler<MouseEvent> {
                button.style = "-fx-underline: true; -fx-font-family: 'MS Gothic'; -fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-background-color: #294a7a; -fx-border-radius: 0;"
            }

            button.onMouseExited = EventHandler<MouseEvent> {
                button.style = "-fx-font-family: 'MS Gothic'; -fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-background-color: #294a7a; -fx-border-radius: 0;"
            }

            button.onAction = EventHandler { addNode(buttonType) }
            hbox.children.add(button)
        }

        return hbox

    }

    private fun addNode(nodeType: String) {
        val node = when (nodeType) {
            "[open]" -> InputNode()
            "[float]" -> FloatNode()
            "[int]" -> IntNode()
            "[string]" -> StringNode()
            "[add text]" -> AddTextNode()
            "[add image]" -> AddImageNode()
            "[grey]" -> GreyFilterNode()
            "[brightness]" -> BrightnessFilterNode()
            "[sepia]" -> SepiaFilterNode()
            "[invert]" -> InvertFilterNode()
            "[blur]" -> BlurFilterNode()
            "[move]" -> MoveTransformNode()
            "[scale]" -> ScaleTransformNode()
            else -> RotateTransformNode()
        }

        node.layoutX = 50.0
        node.layoutY = 50.0
        node.style = "-fx-border-radius: 0; -fx-border-width: 1; -fx-border-color: #c5c5c5; -fx-background-color: #323232;"

        root.children.add(node)

    }
}

class GraphicEditor : Application() {
    override fun start(primaryStage: Stage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        primaryStage.scene = Builder().start()
        primaryStage.isMaximized = true
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(GraphicEditor::class.java)
        }
    }
}