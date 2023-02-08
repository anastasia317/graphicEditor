package com.example.graphiceditor

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class AddTextNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    @FXML
    var thirdLink: AnchorPane? = null

    @FXML
    var fourthLink: AnchorPane? = null

    override fun addInit() {
        titleBar!!.text = "[add text]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.INT)
        nodes["thirdLink"] = Triple(thirdLink!!, null, NodeTypes.INT)
        nodes["fourthLink"] = Triple(fourthLink!!, null, NodeTypes.STRING)

        (secondLink!!.children.find { it is Label } as Label).text = "int x"
        (thirdLink!!.children.find { it is Label } as Label).text = "int y"
        (fourthLink!!.children.find { it is Label } as Label).text = "str"
    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val x = nodes["secondLink"]!!.second?.getValue() as Int? ?: return emptyMat()
        val y = nodes["thirdLink"]!!.second?.getValue() as Int? ?: return emptyMat()
        val text = nodes["fourthLink"]!!.second?.getValue() as String? ?: return emptyMat()
        val dst = Mat()
        src.copyTo(dst)

        val position = Point(x.toDouble(), y.toDouble())
        val color = Scalar(255.0, 0.0, 0.0)
        val thickness = 3
        Imgproc.putText(dst, text, position, 0, 5.0, color, thickness)

        return dst
    }

    init {
        init("FourLinksNode.fxml")
    }
}

class AddImageNode : ImageNode() {
    @FXML
    var secondLink: AnchorPane? = null

    @FXML
    var thirdLink: AnchorPane? = null

    @FXML
    var fourthLink: AnchorPane? = null

    override fun addInit() {
        titleBar!!.text = "[add image]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes["secondLink"] = Triple(secondLink!!, null, NodeTypes.IMAGE)
        nodes["thirdLink"] = Triple(thirdLink!!, null, NodeTypes.INT)
        nodes["fourthLink"] = Triple(fourthLink!!, null, NodeTypes.INT)

        (secondLink!!.children.find { it is Label } as Label).text = "second image"
        (thirdLink!!.children.find { it is Label } as Label).text = "int x"
        (fourthLink!!.children.find { it is Label } as Label).text = "int y"
    }

    override fun getValue(): Mat? {
        val src1 = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val src2 = nodes["secondLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val ix = nodes["thirdLink"]!!.second?.getValue() as Int? ?: return emptyMat()
        val iy = nodes["fourthLink"]!!.second?.getValue() as Int? ?: return emptyMat()

        val img = Mat()
        src1.copyTo(img)

        val imageData = ByteArray(((src1.total() * src1.channels()).toInt()))
        src1.get(0, 0, imageData)
        val imageData2 = ByteArray(((src2.total() * src2.channels()).toInt()))
        src2.get(0, 0, imageData2)
        val y = 0.0
        while (y >= 0 && y <= src2.rows()) {

        }

        for (y in 0 until src2.rows()) {
            for (x in 0 until src2.cols()) {
                for (c in 0 until src2.channels()) {
                    if (0 <= iy + y && iy + y < src1.rows() && 0 <= ix + x && ix + x < src1.cols()) {
                        imageData[((iy + y) * src1.cols() + ix + x) * src1.channels() + c] =
                            imageData2[(y * src2.cols() + x) * src2.channels() + c]
                    }
                }
            }
        }
        img.put(0, 0, imageData)
        return img
    }

    init {
        init("FourLinksNode.fxml")
    }
}