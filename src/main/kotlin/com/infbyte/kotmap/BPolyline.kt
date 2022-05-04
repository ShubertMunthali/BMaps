package com.infbyte.kotmap

class BPolyline(){

    var points = mutableListOf<BPoint>()

    constructor(BPoints:MutableList<BPoint>):this(){
        if(BPoints.isNotEmpty()) {
            this.points.addAll(BPoints)
        }
    }

}