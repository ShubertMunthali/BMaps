package com.infbyte.kotmap

import java.awt.Color

class BMapLayer {
    var type = ""
    var fillColor: Color? = null
    var outlineColor: Color? = null
    var name = ""
    val coordinates = mutableListOf<BPoint>()

    companion object GeometryTypes{
        const val POLYGON = "Polygon"
        const val POLYLINE = "Polyline"
        const val POINT = "Point"
    }
}