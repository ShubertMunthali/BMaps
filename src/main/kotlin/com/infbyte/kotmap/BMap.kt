package com.infbyte.kotmap

import com.infbyte.kotmap.BMap.Extensions.GEOJSON
import com.infbyte.parsers.BGeoJSONParser
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.io.File
import java.nio.file.Files
import javax.swing.Icon
import kotlin.io.path.Path
import kotlin.io.path.extension

open class BMap(private val geoParser: BGeoJSONParser, val proj:KotMapProj): Icon{

    var mapWidth = 10000000
    var mapHeight = mapWidth
    var x = 0
    var y = 0
    lateinit var graphics:Graphics2D

    private val dataFiles = mutableListOf<File>()
    val mapLayers = mutableListOf<BMapLayer>()

    init{
        proj.scaleFactor = 0.01
        fetchFiles()
     }

    private fun createMapLayers(){
        println(dataFiles.size)
       for(file in dataFiles){
            val layer = geoParser.createLayerFromFile(file)
            mapLayers.add(layer)
        }
    }

    private fun fetchFiles(){
        //val absPath = Path("C:\\Users\\User\\IdeaProjects\\BMaps\\src\\main\\kotlin\\com\\infbyte\\data")
        val relPath = Path("./data")

        Files.list(relPath).forEach {
            if(it.fileName.extension == GEOJSON) {
                println("Fetching files...")
                val file = it.toFile()
                dataFiles.add(file)
            }
        }
        createMapLayers()
    }

    private fun configureGraphicsForMaps(graphics:Graphics2D ){
        graphics.translate(0.0, this.mapHeight * proj.scaleFactor)
        graphics.scale(proj.scaleFactor, -proj.scaleFactor)
    }

    override fun paintIcon(c: Component?, graphics: Graphics?, x: Int, y: Int) {
        this.graphics = graphics as Graphics2D
        configureGraphicsForMaps(this.graphics)
        this.x = x
        this.y = y
        this.graphics.dispose()
    }

    override fun getIconWidth(): Int {
        val iconWidth = mapWidth * proj.scaleFactor
        return iconWidth.toInt()
    }

    override fun getIconHeight(): Int {
        val iconHeight = mapHeight * proj.scaleFactor
        return iconHeight.toInt()
    }

    object Extensions{
        const val GEOJSON = "geojson"
    }

}