package com.infbyte.kotmap


import com.infbyte.bmap.color.BMapColor
import com.infbyte.bmap.font.BMapsFont
import com.infbyte.bmap.ui.listeners.CanvasMouseListener
import java.awt.*
import java.awt.geom.AffineTransform
import java.lang.NullPointerException
import javax.swing.JLabel
import javax.swing.border.LineBorder

class BMapCanvas(val bMap:BMap): JLabel(bMap) {

    private lateinit var graphics:Graphics2D
    private lateinit var bMapRenderer:KotMapRenderer
    private val points = mutableListOf<BPoint>()
    private val polyLines = mutableListOf<BPolyline>()
    private val polygons = mutableListOf<BPolygon>()

    init{
        mapLayersToFeatures()
    }

    private fun mapLayersToFeatures(){
        for(layer in bMap.mapLayers){
            println(layer.type)
            when(layer.type ) {
                BMapLayer.POINT ->{
                    points.addAll(layer.coordinates)
                }
                BMapLayer.POLYLINE ->{
                    val polyline = BPolyline(layer.coordinates)
                    polyLines.add(polyline)
                }
                BMapLayer.POLYGON ->{
                    val polygon = BPolygon(layer.coordinates)
                    polygons.add(polygon)
                }
            }
            for(coord in layer.coordinates){
                println("${coord.x}, ${coord.y}")
            }
        }
    }

    private fun configureGraphicsForMaps(){
        graphics.translate(0.0, bMap.mapHeight * bMap.proj.scaleFactor)
        graphics.scale(bMap.proj.scaleFactor, -bMap.proj.scaleFactor)
    }

    private fun resetGraphics(resetTx: AffineTransform){
        graphics.transform = resetTx
    }

    private fun drawBackground(){
        graphics.color = BMapColor.background
        graphics.fillRect(x, y, this.bMap.mapWidth, this.bMap.mapHeight)
    }

    override fun paint(graphics: Graphics?) {
        super.paint(graphics)
        this.graphics = graphics as Graphics2D
        bMapRenderer = KotMapRenderer(this.graphics)
        val resetTx = this.graphics.transform

        configureGraphicsForMaps()
        drawBackground()

        bMapRenderer.apply {
            drawPointsOnMap(points)
            drawLinesOnMap(polyLines)
            drawPolygonsOnMap(polygons)
            drawFilledPolygons(polygons)
        }

        resetGraphics(resetTx)

        this.graphics.apply {
            color = BMapColor.text
            font = BMapsFont.verdana
            drawString("Blantyre", 7040 * 2,17350 * 2)//705400 * bMap.proj.scaleFactor.toFloat(), (10000000-8266100)*bMap.proj.scaleFactor.toFloat())
        }

        this.graphics.dispose()
    }

    /*override fun scrollRectToVisible(aRect: Rectangle?) {
        super.scrollRectToVisible(aRect)
        this.graphics.apply {
            color = BMapColor.text
            font = BMapsFont.verdana
            drawString("Blantyre", 7040 * 2,17350 * 2)//705400 * bMap.proj.scaleFactor.toFloat(), (10000000-8266100)*bMap.proj.scaleFactor.toFloat())
        }
    }*/

}
