package com.infbyte.bmap.ui.listeners

import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import javax.swing.JComponent
import javax.swing.JViewport

class CanvasMouseListener: MouseAdapter() {

    var canvas:JComponent? = null
    var viewport:JViewport? = null
    var startMousePosition:Point? = null

    override fun mousePressed(e: MouseEvent?) {
        super.mousePressed(e)
        canvas = e?.source as JComponent
        startMousePosition = canvas?.mousePosition
    }

    override fun mouseDragged(e: MouseEvent?) {
        super.mouseDragged(e)
        canvas = e?.source as JComponent
        viewport = canvas?.parent as JViewport
        val viewportPosition = viewport?.viewPosition
        val newMousePosition = canvas?.mousePosition
        val dx = startMousePosition?.x?.minus(newMousePosition?.x!!)
        val dy = startMousePosition?.y?.minus(newMousePosition?.y!!)
        viewportPosition?.translate( startMousePosition?.x?.minus(newMousePosition?.x!!)!!, startMousePosition?.y?.minus(newMousePosition?.y!!)!!)
        val visibleViewRect = Rectangle(viewportPosition!!, viewport?.size!!)
        canvas?.scrollRectToVisible(visibleViewRect)
        startMousePosition?.location = newMousePosition
    }

    override fun mouseClicked(e: MouseEvent?) {
        super.mouseClicked(e)
        if(e?.clickCount == 2){
            canvas = e.source as JComponent
            viewport = canvas?.parent as JViewport
            val canvasHeight = canvas?.height!!
            val visibleViewRect = Rectangle(Point(725356/100, (canvasHeight - 8246297)/100), viewport?.size!!)
            canvas?.scrollRectToVisible(visibleViewRect)
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
        super.mouseMoved(e)
        canvas = e?.source as JComponent
        val viewport:JViewport  = canvas?.parent as JViewport
        val canvasHeight = canvas?.height
        println("${canvas?.mousePosition?.x}, ${canvasHeight!! - canvas?.mousePosition?.y!!}")
    }
}