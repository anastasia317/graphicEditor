package com.example.graphiceditor

import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.*
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.CubicCurve
import org.opencv.core.Mat
import java.io.IOException
import java.util.*

lateinit var nodeDragStart: DraggableNode
lateinit var anchorDragStart: AnchorPane

var stateAddLink = DataFormat("linkAdd")
var stateAddNode = DataFormat("nodeAdd")

abstract class DraggableNode : AnchorPane() {
    @FXML
    var rootPane: AnchorPane? = null

    @FXML
    var outputLinkHandle: AnchorPane? = null

    @FXML
    var titleBar: Label? = null

    @FXML
    var deleteButton: Button? = null

    lateinit var contextDragOver: EventHandler<DragEvent>
    lateinit var contextDragDropped: EventHandler<DragEvent>

    lateinit var linkDragDetected: EventHandler<MouseEvent>
    lateinit var linkDragDropped: EventHandler<DragEvent>
    lateinit var contextLinkDragOver: EventHandler<DragEvent>
    lateinit var contextLinkDagDropped: EventHandler<DragEvent>

    private var myLink = NodeLink()
    private var offset = Point2D(0.0, 0.0)

    var connectedLinks = mutableListOf<NodeLink>()
    var outputLink: NodeLink? = null

    var nodes = mutableMapOf<String, Triple<AnchorPane, DraggableNode?, NodeTypes>>()
    open val nodeType = NodeTypes.NONE

    var superParent: AnchorPane? = null

    @FXML
    private fun initialize() {
        nodeHandlers()
        linkHandlers()

        initHandles()
        addInit()

        myLink.isVisible = false

        deleteButton!!.onAction = EventHandler {
            superParent!!.children.remove(this)
            superParent!!.children.removeIf { ch -> connectedLinks.count { it.id == ch.id } > 0 }
        }

        nodes.forEach { it.value.first.onDragDropped = linkDragDropped }

        parentProperty().addListener { _, _, _ -> if (parent != null) superParent = parent as AnchorPane }
    }

    open fun initHandles() {
        if (outputLinkHandle != null)
            outputLinkHandle!!.onDragDetected = linkDragDetected
    }

    open fun addInit() {}

    private fun updatePoint(p: Point2D) {
        val local = parent.sceneToLocal(p)
        relocate(
            (local.x - offset.x),
            (local.y - offset.y)
        )
    }

    private fun nodeHandlers() {

        contextDragOver = EventHandler { event ->
            updatePoint(Point2D(event.sceneX, event.sceneY))
            event.consume()
        }

        contextDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null
            event.isDropCompleted = true
            event.consume()
        }


        rootPane!!.onDragDetected = EventHandler { event ->
            parent.onDragOver = contextDragOver
            parent.onDragDropped = contextDragDropped

            offset = Point2D(event.x, event.y)
            updatePoint(Point2D(event.sceneX, event.sceneY))

            val content = ClipboardContent()
            content[stateAddNode] = "node"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
            event.consume()
        }
    }

    private fun linkNodes(firstNode: DraggableNode, secondNode: DraggableNode, linkStart: AnchorPane, linkEnd: AnchorPane, nodeId: String): NodeLink {
        val link = NodeLink()

        superParent!!.children.add(0, link)
        firstNode.outputLink = link

        nodes[nodeId] = nodes[nodeId]!!.copy(second = firstNode)
        link.bindStartEnd(firstNode, secondNode, linkStart, linkEnd)
        return link
    }

    private fun linkHandlers() {

        linkDragDetected = EventHandler { event ->

            if (outputLink != null) {
                superParent!!.children.remove(outputLink)
            }

            parent.onDragOver = null
            parent.onDragDropped = null

            parent.onDragOver = contextLinkDragOver
            parent.onDragDropped = contextLinkDagDropped

            superParent!!.children.add(0, myLink)
            myLink.isVisible = true

            val src = event.source as AnchorPane

            val p = Point2D(layoutX + src.layoutX + src.width / 2, layoutY + src.layoutY + src.height / 2)
            myLink.setStart(p)

            nodeDragStart = this
            anchorDragStart = event.source as AnchorPane

            val content = ClipboardContent()
            content[stateAddLink] = "link"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
            event.consume()
        }

        linkDragDropped = EventHandler { event ->

            val src = event.source as AnchorPane
            if (nodeDragStart == this || !nodes.containsKey(src.id) || nodeDragStart.nodeType != nodes[src.id]!!.third || nodes[src.id]!!.second != null)
                return@EventHandler

            parent.onDragOver = null
            parent.onDragDropped = null

            myLink.isVisible = false
            superParent!!.children.removeAt(0)

            linkNodes(nodeDragStart, this, anchorDragStart, event.source as AnchorPane, src.id)

            event.isDropCompleted = true
            event.consume()
        }


        contextLinkDragOver = EventHandler { event ->
            event.acceptTransferModes(*TransferMode.ANY)
            if (!myLink.isVisible) myLink.isVisible = true
            myLink.setEnd(Point2D(event.x, event.y))

            event.consume()
        }

        contextLinkDagDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null

            myLink.isVisible = false
            superParent!!.children.removeAt(0)

            event.isDropCompleted = true
            event.consume()
        }
    }

    fun init(str: String) {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource(str)
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
        id = UUID.randomUUID().toString()
    }

    abstract fun getValue(): Any?

    abstract fun updateNode()

    fun emptyMat(): Mat? {
        return null
    }

    open fun toData(): NodeData {
        return NodeData(id, this::class.simpleName!!, rootPane?.layoutX, rootPane?.layoutY, null)
    }

    open fun fromData(nodeData: NodeData) {
        id = nodeData.id
        layoutX = nodeData.x ?: 100.0
        layoutY = nodeData.y ?: 100.0
    }
}

data class NodeData(val id: String, val name: String, val x: Double?, val y: Double?, var data: String?)

class NodeLink : AnchorPane() {
    @FXML
    var nodeLink: CubicCurve? = null

    private var inputLinkString: String = ""
    private var inputNode: DraggableNode? = null
    private var outputNode: DraggableNode? = null
    private var inputAnchor: AnchorPane? = null
    private var outputAnchor: AnchorPane? = null

    @FXML
    private fun initialize() {
        nodeLink!!.controlX1Property().bind(Bindings.add(nodeLink!!.startXProperty(), 100))
        nodeLink!!.controlX2Property().bind(Bindings.add(nodeLink!!.endXProperty(), -100))
        nodeLink!!.controlY1Property().bind(Bindings.add(nodeLink!!.startYProperty(), 0))
        nodeLink!!.controlY2Property().bind(Bindings.add(nodeLink!!.endYProperty(), 0))

        parentProperty().addListener { _, _, _ ->
            if (parent == null) {
                if (inputNode != null) {
                    inputNode!!.connectedLinks.remove(this)
                    if (outputNode != null && inputNode!!.nodes.containsKey(inputLinkString)) {
                        inputNode!!.nodes[inputLinkString] =
                            inputNode!!.nodes[inputLinkString]!!.copy(second = null)
                    }
                }
                if (outputNode != null) {
                    outputNode!!.connectedLinks.remove(this)
                    outputNode!!.outputLink = null
                }
            }
        }
    }

    fun setStart(point: Point2D) {
        nodeLink!!.startX = point.x
        nodeLink!!.startY = point.y
    }

    fun setEnd(point: Point2D) {
        nodeLink!!.endX = point.x
        nodeLink!!.endY = point.y
    }

    fun bindStartEnd(firstNode: DraggableNode, secondNode: DraggableNode, linkStart: AnchorPane, linkEnd: AnchorPane) {
        nodeLink!!.startXProperty().bind(Bindings.add(firstNode.layoutXProperty(), linkStart.layoutX + linkStart.width / 2.0))
        nodeLink!!.startYProperty().bind(Bindings.add(firstNode.layoutYProperty(), linkStart.layoutY + linkStart.height / 2.0))
        nodeLink!!.endXProperty().bind(Bindings.add(secondNode.layoutXProperty(), linkEnd.layoutX + linkEnd.width / 2.0))
        nodeLink!!.endYProperty().bind(Bindings.add(secondNode.layoutYProperty(), linkEnd.layoutY + linkEnd.height / 2.0))

        inputLinkString = linkEnd.id
        outputAnchor = linkStart
        inputAnchor = linkEnd
        links(firstNode, secondNode)
    }

    private fun links(firstNode: DraggableNode, secondNode: DraggableNode) {
        outputNode = firstNode
        inputNode = secondNode
        firstNode.connectedLinks.add(this)
        secondNode.connectedLinks.add(this)

        if (updateNode(outputNode!!))
            kickAction()
    }

    private fun updateNode(node: DraggableNode): Boolean {
        if (node.nodes.all { it.value.second != null }) {
            node.updateNode()
            return true
        }
        return false
    }

    fun kickAction() {
        if (inputNode == null)
            return

        if (updateNode(inputNode!!) && inputNode!!.outputLink != null)
            inputNode!!.outputLink!!.kickAction()
    }

    fun toData(): LinkData {
        return LinkData(
            id,
            inputNode?.id,
            inputNode!!::class.simpleName,
            outputNode?.id,
            outputNode!!::class.simpleName,
            inputAnchor?.id,
            PairD(inputAnchor!!.layoutX + inputAnchor!!.width / 2, inputAnchor!!.layoutY + inputAnchor!!.height / 2),
            outputAnchor?.id,
            PairD(
                outputAnchor!!.layoutX + outputAnchor!!.width / 2,
                outputAnchor!!.layoutY + outputAnchor!!.height / 2
            ),
        )
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource("NodeLink.fxml")
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
        id = UUID.randomUUID().toString()
    }
}

data class PairD<A, B>(val first: A, val second: B)

data class LinkData(
    val id: String?,
    val inputNode: String?,
    val inputNodeClass: String?,
    val outputNode: String?,
    val outputNodeClass: String?,
    val inputAnchor: String?,
    val inputAnchorSize: PairD<Double, Double>,
    val outputAnchor: String?,
    val outputAnchorSize: PairD<Double, Double>
)