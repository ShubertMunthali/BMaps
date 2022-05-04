package com.infbyte.kotmap

class BPoint(){
/**
 * The <code>BPoint</code> class is a public class for holding all com.infbyte.data that is a point in graphics context.
 * It holds points of Int, Float and Double com.infbyte.data types. The point instance can be created by the primary constructor.
 * The secondary constructors are used to instantiate the class with com.infbyte.data type parameters.
 * @author      Shubert Martin Munthali
 * */
    constructor(x:Int,  y:Int):this(){
        this.x = x.toDouble()
        this.y = y.toDouble()
        IntB().apply{
            this.x = x
            this.y = y
        }

    }

    constructor (x: Float, y: Float):this(){
        this.x = x.toDouble()
        this.y = y.toDouble()
        FloatB().apply {
            this.x = x
            this.y = y
        }
    }

    constructor(x:Double, y:Double):this(){
        this.x = x
        this.y = y
        DoubleB().apply{
            this.x  = x
            this.y = y
        }

    }

    /**
     * Generates the getters and setters for the double type x  and y values of the current BPoint instance
     * */
    var x = 0.00
    var y = 0.00

    /**
     * Returns the integer x value of the current BPoint instance
     * */
    fun getX():Int{
        return x.toInt()
    }

    /**
     * Returns the integer y value of the current BPoint instance
     * */
    fun getY():Int{
        return y.toInt()
    }

    /**
     * Returns the float x value of the current BPoint instance
     * */
    fun getXF():Float{
        return x.toFloat()
    }

    /**
     * Returns the float y value of the current BPoint instance
     * */
    fun getYF():Float{
        return y.toFloat()
    }

    private class IntB{
        var x = 0
        var y = 0
    }

    private class FloatB {
        var x: Float = 0.00F
        var y: Float = 0.00F
    }

    private class DoubleB{
        var x:Double = 0.00
        var y:Double = 0.00
    }
}