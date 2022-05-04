package com.infbyte.kotmap

import com.infbyte.bmap.color.BMapColor
import com.infbyte.bmap.color.BMapColor.white
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D

class KotMapRenderer(private val graphics: Graphics2D) {

    private var numberOfPoints = 0
    private var pointIndex = 0
    private var x1 = 0.0
    private var y1 = 0.0
    private var x2 = 0.0
    private var y2 = 0.0
    private lateinit var line:Line2D

    private fun drawPointOnMap(bPoint:BPoint){
        graphics.color = white
        graphics.scale(2.0, 2.0)
        bPoint.apply {
            line = Line2D.Double(x, y, x, y)
            graphics.draw(line)
        }
    }

    private fun drawPolylineOnMap(bPoints:MutableList<BPoint>){
        numberOfPoints = bPoints.size
        pointIndex = 0
        for(i in 0 until (numberOfPoints - 1)){
            x1 = bPoints[pointIndex].x
            y1 = bPoints[pointIndex].y

            pointIndex += 1

            x2 = bPoints[pointIndex].x
            y2 = bPoints[pointIndex].y
            line = Line2D.Double(x1, y1, x2, y2)
            graphics.draw(line)
         }
    }

    private fun drawPolygonOnMap(bPoints:MutableList<BPoint>){
        numberOfPoints = bPoints.size
        pointIndex = 0
        for(point in bPoints){
            x1 = bPoints[pointIndex].x
            y1 = bPoints[pointIndex].y
            if(pointIndex < numberOfPoints - 1) {
                pointIndex += 1
            }else{
                pointIndex = 0
            }
            x2 = bPoints[pointIndex].x
            y2 = bPoints[pointIndex].y
            line = Line2D.Double(x1, y1, x2, y2)
            graphics.draw(line)
        }
    }

    private fun fillPolygonOnMap(bPoints:MutableList<BPoint>){
       numberOfPoints = bPoints.size
        pointIndex = 0
        graphics.color = BMapColor.ground
        val polygonPath = Path2D.Double()
        polygonPath.moveTo(bPoints[0].x, bPoints[0].y)
        for(point in bPoints){
            if(pointIndex < numberOfPoints - 1){
                pointIndex++
                x2 = bPoints[pointIndex].x
                y2 = bPoints[pointIndex].y
                polygonPath.lineTo(x2, y2)
            }else{
                polygonPath.closePath()
            }
        }
        graphics.fill(polygonPath)
    }

    private fun styleLines(){

    }

    fun drawPointsOnMap(bPoints:MutableList<BPoint>){
        for(point in bPoints){
            drawPointOnMap(point)
        }
    }

    fun drawLinesOnMap(lines:MutableList<BPolyline>){
        for(line in lines){
            drawPolylineOnMap(line.points)
        }
    }

    fun drawPolygonsOnMap(bPolygons:MutableList<BPolygon>){
        for(polygon in bPolygons){
            drawPolygonOnMap(polygon.points)
        }
    }

    fun drawFilledPolygons(bPolygons:MutableList<BPolygon>){
        for(polygon in bPolygons){
            fillPolygonOnMap(polygon.points)
        }
    }
}