package com.example.graphiceditor

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ScaleTransformNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    @FXML
    var thirdLink: AnchorPane? = null
    override fun addInit() {
        titleBar!!.text = "[scale]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.FLOAT)
        nodes["thirdLink"] = Triple(thirdLink!!, null, NodeTypes.FLOAT)

        (secondLink!!.children.find { it is Label } as Label).text = "float x"
        (thirdLink!!.children.find { it is Label } as Label).text = "float y"
    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val x = nodes["secondLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val y = nodes["thirdLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val dst = Mat()

        if (x <= 0 || y <= 0) return null

        Imgproc.resize(src, dst, Size(x.toDouble(), y.toDouble()))

        return dst
    }

    init {
        init("ThreeNodesIV.fxml")
    }

}

class MoveTransformNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    @FXML
    var thirdLink: AnchorPane? = null
    override fun addInit() {
        titleBar!!.text = "[move]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.FLOAT)
        nodes["thirdLink"] = Triple(thirdLink!!, null, NodeTypes.FLOAT)

        (secondLink!!.children.find { it is Label } as Label).text = "float x"
        (thirdLink!!.children.find { it is Label } as Label).text = "float y"
    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val x = nodes["secondLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val y = nodes["thirdLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val dst = Mat()
        val moveMat = Mat(2, 3, CvType.CV_64FC1)
        moveMat.put(0, 0, 1.0, 0.0, (src.cols() * x / 100.0), 0.0, 1.0, (src.rows() * y / 100.0))

        Imgproc.warpAffine(src, dst, moveMat, Size(src.cols().toDouble(), src.rows().toDouble()))

        return dst
    }

    init {
        init("ThreeNodesIV.fxml")
    }

}
