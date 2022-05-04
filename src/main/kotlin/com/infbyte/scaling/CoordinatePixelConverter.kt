package com.infbyte.scaling

class CoordinatePixelConverter {

    fun getPixels(measure:Double):Double{
        return measure / PIXEL_EQUIVALENT
    }

    companion object{
        const val PIXEL_EQUIVALENT = 0.000264583333337192
    }
}