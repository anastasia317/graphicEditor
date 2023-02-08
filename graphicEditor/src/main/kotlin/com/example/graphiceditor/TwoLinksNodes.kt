package com.example.graphiceditor

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class BrightnessFilterNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    override fun addInit() {
        titleBar!!.text = "[brightness]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.FLOAT)

        (secondLink!!.children.find { it is Label } as Label).text = "float bright"

    }


    override fun getValue(): Mat? {

        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val beta = nodes["secondLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val dst = Mat()
        val img = Mat()
        src.convertTo(dst, -1, 1.0, beta.toDouble())
        dst.copyTo(img)
        return img
    }

    init {
        init("TwoLinksNode.fxml")
    }
}

class BlurFilterNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    override fun addInit() {
        titleBar!!.text = "[blur]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.INT)

        (secondLink!!.children.find { it is Label } as Label).text = "int kernelSize"
    }


    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        var kernelSize = nodes["secondLink"]!!.second?.getValue() as Int? ?: return emptyMat()

        if (kernelSize % 2 == 0) kernelSize += 1

        if (kernelSize <= 0) return null

        val dst = Mat()
        Imgproc.GaussianBlur(src, dst, Size(kernelSize.toDouble(), kernelSize.toDouble()), 0.0, 0.0)

        return dst
    }

    init {
        init("TwoLinksNode.fxml")
    }
}

class RotateTransformNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    override fun addInit() {
        titleBar!!.text = "[rotate]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.FLOAT)

        (secondLink!!.children.find { it is Label } as Label).text = "float rad"
    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val rad = nodes["secondLink"]!!.second?.getValue() as Float? ?: return emptyMat()
        val dst = Mat()
        val rotPoint = Point(src.cols() / 2.0, src.rows() / 2.0)
        val rotMat = Imgproc.getRotationMatrix2D(rotPoint, rad.toDouble(), 1.0)

        Imgproc.warpAffine(src, dst, rotMat, Size(src.cols().toDouble(), src.rows().toDouble()))

        return dst
    }

    init {
        init("TwoLinksNode.fxml")
    }
}