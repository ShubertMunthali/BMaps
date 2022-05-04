package com.infbyte.kotmap

import java.awt.Polygon

class BPolygon():Polygon() {
    val points = mutableListOf<BPoint>()

    constructor(BPoints:MutableList<BPoint>):this(){
        if(BPoints.isNotEmpty()) {
            this.points.addAll(BPoints)
        }
    }

    fun setPoints(BPoints:MutableList<BPoint>){
        if(this.points.isNotEmpty()){
            this.points.clear()
            this.points.addAll(BPoints)
        }else{
            this.points.addAll(BPoints)
        }
    }
}