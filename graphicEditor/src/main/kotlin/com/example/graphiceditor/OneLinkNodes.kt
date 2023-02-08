package com.example.graphiceditor

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class SepiaFilterNode : ImageNode() {
    override fun addInit() {
        titleBar!!.text = "[sepia]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)

    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val img = Mat()
        src.copyTo(img)
        val mSepiaKernel = Mat(3, 3, CvType.CV_64FC1)
        val row = 0
        val col = 0
        mSepiaKernel.put(row, col, 0.272, 0.534, 0.131, 0.349, 0.686, 0.168, 0.393, 0.769, 0.189)
        Core.transform(src, img, mSepiaKernel)

        return img
    }

    init {
        init("OneLinkNode.fxml")
    }
}

class InvertFilterNode : ImageNode() {
    override fun addInit() {
        titleBar!!.text = "[invert]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)

    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val img = Mat()
        src.copyTo(img)
        Core.bitwise_not(src, img)

        return img
    }

    init {
        init("OnelinkNode.fxml")
    }
}

class GreyFilterNode : ImageNode() {
    override fun addInit() {
        titleBar!!.text = "[grey]"

        nodes["firstLink"] = Triple(firstLink!!, null, NodeTypes.IMAGE)

    }

    override fun getValue(): Mat? {
        val src = nodes["firstLink"]!!.second?.getValue() as Mat? ?: return emptyMat()
        val dst = Mat()
        val img = Mat()
        src.copyTo(dst)
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY)
        dst.copyTo(img)

        return img
    }

    init {
        init("OneLinkNode.fxml")
    }
}