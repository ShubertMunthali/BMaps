import com.infbyte.bmap.ui.BMapUI
import com.infbyte.kotmap.*
import com.infbyte.parsers.BGeoJSONParser
import java.awt.EventQueue


fun main(){
    val kotMapProj = KotMapProj()
    kotMapProj.scaleFactor = 1/20000.0
    val proj = kotMapProj.UniversalTransverseMercator().WGS84RefSpheroid()
    val tPoint = proj.toCartesianCoordinates(36.0,-16.0)
    println("(${tPoint.x}, ${tPoint.y})")
    val geoParser = BGeoJSONParser()
    //val layer = geoParser.createLayerFromFile(File("C:\\Users\\User\\IdeaProjects\\BMaps\\src\\main\\kotlin\\com\\pitech\\data\\blantyre.geojson"))
    //println("${layer.coordinates[0].x}, ${layer.coordinates[0].y}")
    val bMap = BMap(geoParser, kotMapProj)
    EventQueue.invokeLater {
        run{
            BMapUI(bMap)
        }
    }
}
