package com.infbyte.bmap.ui.listeners

import com.infbyte.bmap.ui.listeners.ViewportMouseListener.ScalingFactors.DOUBLE
import com.infbyte.kotmap.BMapCanvas
import com.infbyte.kotmap.BMapLayer
import com.infbyte.kotmap.BPoint
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JComponent
import javax.swing.JViewport

class ViewportMouseListener: MouseAdapter(){

    private var startMousePosition =  Point()
    private var newMousePosition = Point()
    private var viewportPosition = Point()
    private var viewport:JViewport? = null
    private var canvas: BMapCanvas? = null
    private val visibleViewRect = Rectangle()
    private var canvasHeight = 0
    private var rotation = 0

    override fun mousePressed(e: MouseEvent?) {
        super.mousePressed(e)
        viewport = e?.source as JViewport
        canvas = viewport?.view as BMapCanvas
        startMousePosition = canvas?.mousePosition!!
    }

    override fun mouseDragged(e: MouseEvent?) {
        super.mouseDragged(e)
        viewport = e?.source as JViewport
        canvas = viewport?.view as BMapCanvas
        try{
            newMousePosition = canvas?.mousePosition!!
            viewportPosition = viewport?.viewPosition!!
            println("${viewportPosition.x}, ${viewportPosition.y}")
            viewportPosition.translate(
                startMousePosition.x - newMousePosition.x,
                startMousePosition.y - newMousePosition.y
            )
            visibleViewRect.location = viewportPosition
            visibleViewRect.size = viewport?.size!!
            canvas?.scrollRectToVisible(visibleViewRect)
            println("${canvas?.mousePosition?.getX()}, ${(canvas?.height?.toDouble()!!/100) - canvas?.mousePosition?.getY()!!}")
            println("CanvasX: ${canvas?.x}, CanvasY: ${canvas?.y}")
            println("ViewportX: ${viewportPosition.x}, ViewportY: ${viewportPosition.y}")
        }
        catch(e:NullPointerException){
            println("The pointer is outside the viewport!")
        }
    }

    override fun mouseClicked(e: MouseEvent?) {
        super.mouseClicked(e)
        if(e?.clickCount!! == 2) {
            viewport = e.source as JViewport
            canvas = viewport?.view as BMapCanvas
            canvasHeight = canvas?.height!!
            val visibleRect = Rectangle(Point((637656 * 1/100),canvasHeight - (8299197 * 1/100)), viewport?.size!!)
            canvas?.scrollRectToVisible(visibleRect)
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
        super.mouseMoved(e)
        viewport = e?.source as JViewport
        canvas = viewport?.view as BMapCanvas
        canvasHeight = canvas?.height!!
        newMousePosition = canvas?.mousePosition!!
        println("${canvas?.mousePosition?.getX()}, ${/*canvasHeight -*/ canvas?.mousePosition?.getY()!!}")
    }

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        super.mouseWheelMoved(e)
        viewport = e?.source as JViewport
        canvas = viewport?.view as BMapCanvas
        val viewportMousePosition = viewport?.mousePosition
        val wheelMove = e.wheelRotation
        val zoomIn = wheelMove < 0
        val zoomOut = wheelMove > 0
        var scale = canvas?.bMap?.proj?.scaleFactor
            when {
                zoomIn -> {
                    println("Zooming in...")
                    viewportPosition.x = newMousePosition.x.times(DOUBLE).minus(viewportMousePosition?.x!!)
                    viewportPosition.y = newMousePosition.y.times(DOUBLE).minus(viewportMousePosition.y)
                    if(viewportPosition.x != 0 && viewportPosition.y != 0) {
                        scale = scale?.times(DOUBLE)!!
                    }
                }
                zoomOut -> {
                    println("Zooming out...")
                    viewportPosition.x = newMousePosition.x.div(DOUBLE).minus(viewportMousePosition?.x!!)
                    viewportPosition.y = newMousePosition.y.div(DOUBLE).minus(viewportMousePosition.y)
                    if(viewportPosition.x != 0 && viewportPosition.y != 0) {
                        scale = scale?.div(DOUBLE)!!
                    }
                }
            }

        if(viewportPosition.x != 0 && viewportPosition.y != 0) {
            visibleViewRect.location = viewportPosition
            visibleViewRect.size = viewport?.size!!
            canvas?.bMap?.proj?.scaleFactor = scale!!
            canvas?.scrollRectToVisible(visibleViewRect)
            newMousePosition = canvas?.mousePosition!!
            println(scale)

            println("CanvasX: ${canvas?.x}, CanvasY: ${canvas?.y}")
            println("ViewportX: ${viewportPosition.x}, ViewportY: ${viewportPosition.y}")
        }
        println("Mouse X: ${canvas?.mousePosition?.x}, Mouse Y: ${canvas?.mousePosition?.y}")
        println("ViewportMouseX: ${viewportMousePosition?.x}, ViewportMouseY: ${viewportMousePosition?.y}")
        println("ViewportX + MousePosX): ${viewportPosition.x + viewportMousePosition?.x!!}, ViewportY + MousePosY: ${viewportPosition.y + viewportMousePosition.y}")
    }

    private object ScalingFactors{
        const val DOUBLE = 2
        const val HALF = 1/2
        const val UNITY = 1
        const val UNITY_ONE = 1.1
    }
}