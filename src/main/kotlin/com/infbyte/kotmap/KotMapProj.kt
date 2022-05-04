package com.infbyte.kotmap

import com.infbyte.kotmap.KotMapProj.CRSReferences.EquatorialMercator.LONGITUDE_REF
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.CentralMeridians.ZN36L_LONGITUDE_REF
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.EccentricityTerms.TERM_0
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.EccentricityTerms.TERM_2
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.EccentricityTerms.TERM_4
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.EccentricityTerms.TERM_6
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.FalseOrigins.FALSE_EASTING
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.FalseOrigins.FALSE_NORTHING_S
import com.infbyte.kotmap.KotMapProj.CRSReferences.UniversalTransverseMercator.ScaleFactors.MAX_SCALE
import com.infbyte.kotmap.KotMapProj.KotProjConstants.MITRE
import com.infbyte.kotmap.KotMapProj.KotProjConstants.TWO
import com.infbyte.kotmap.KotMapProj.GeodeticSpheroids.WGS84.ECCENTRICITY
import com.infbyte.kotmap.KotMapProj.GeodeticSpheroids.WGS84.SEMI_MAJOR
import com.infbyte.kotmap.KotMapProj.KotProjConstants.DEG_180
import com.infbyte.kotmap.KotMapProj.KotProjConstants.FIVE
import com.infbyte.kotmap.KotMapProj.KotProjConstants.FOUR
import com.infbyte.kotmap.KotMapProj.KotProjConstants.ONE
import com.infbyte.kotmap.KotMapProj.KotProjConstants.THREE
import kotlin.math.*

class KotMapProj {

    var scaleFactor = 1.00

    private fun sineLat(radLatitude: Double):Double{
        return sin(radLatitude)
    }

    private fun cosLat(radLatitude: Double):Double{
        return cos(radLatitude)
    }

    private fun tanLat(radLatitude: Double):Double{
        return tan(radLatitude)
    }

    inner class EquatorialMercator {
        inner class WGS84RefSpheroid {

            fun toCartesianCoordinates(longitude: Double, latitude: Double): ProjectedPoint {
                val point = ProjectedPoint()
                point.x = transformLongitude(longitude)
                point.y = transformLatitude(latitude)
                println(scaleFactor)
                return point
            }

            private fun computeRadiusOfCurvature():Double{
                return 0.0
            }

            private fun transformLongitude(longitude: Double): Double {
                var x = 0.00
                val lngRad = toRadians(longitude)
                if (scaleFactor > 0) {
                    x = SEMI_MAJOR * scaleFactor * (lngRad - LONGITUDE_REF)
                }
                return x
            }

            private fun transformLatitude(latitude: Double): Double {

                var y = 0.00
                val latRad = toRadians(latitude)
                fun latTangent():Double{
                    return tan((MITRE + (latRad/TWO)))
                }

                fun latSineRatio(): Double {
                    val numerator = (1 - (ECCENTRICITY * sineLat(latRad)))
                    val denominator = (1 + (ECCENTRICITY * sineLat(latRad)))
                    val ratio = numerator / denominator
                    return ratio.pow((ECCENTRICITY / TWO))
                }

                fun latNaturalLog():Double{
                    val product = latTangent() * latSineRatio()
                    return ln(product)
                }

                if(scaleFactor > 0) {
                    y = SEMI_MAJOR * scaleFactor * latNaturalLog()
                }
                return y
            }
        }
    }

    inner class UniversalTransverseMercator{
        inner class WGS84RefSpheroid{

            fun toCartesianCoordinates(longitude:Double, latitude:Double):ProjectedPoint{
                val point = ProjectedPoint()
                val radLng = toRadians(longitude)
                val radLat = toRadians(latitude)
                val radiusOfCurvature = computeRadiusOfCurvature(radLat)
                fun computeFalseEasting():Double{
                    return (MAX_SCALE * transformLongitude(radiusOfCurvature, radLng, radLat)) + FALSE_EASTING
                }
                fun computeFalseNorthing():Double{
                    return (MAX_SCALE * transformLatitude(radiusOfCurvature, radLng, radLat)) + FALSE_NORTHING_S
                }
                point.x = computeFalseEasting()
                point.y = computeFalseNorthing()
                return point
            }

            private fun transformLongitude(radiusOfCurvature:Double, radLongitude: Double, radLatitude: Double): Double {
                val deltaLambdaCosLat = computeLongitudeDiff(radLongitude) * cosLat(radLatitude)
                val deltaLambdaP3CosLat = computeLongitudeDiff(radLongitude).pow(THREE) * cosLat(radLatitude) / 6
                val tanLatP2 = tanLat(radLatitude).pow(TWO)
                val auxNuSd = computeAuxVarNu(radLatitude)
                return radiusOfCurvature * (deltaLambdaCosLat + (deltaLambdaP3CosLat * (ONE - tanLatP2 + auxNuSd)))
            }

            private fun transformLatitude(radiusOfCurvature: Double, radLongitude: Double, radLatitude:Double):Double{

                fun computeLatitudeDiff(): Double {
                    println("f1 = $TERM_0")
                    println("f2 = $TERM_2")
                    println("f3 = $TERM_4")
                    println("f4 = $TERM_6")
                    return SEMI_MAJOR *
                            ((TERM_0 * radLatitude) -
                                    (TERM_2 * sineLat(TWO * radLatitude)) +
                                    (TERM_4 * sineLat(FOUR * radLatitude)) -
                                    ((TERM_6 * sineLat(6.00 * radLatitude))))
                }

                val latitudeDiff = computeLatitudeDiff()
                println("Dm = $latitudeDiff")
                val auxNuSd = computeAuxVarNu(radLatitude)
                val deltaP2Term = computeLongitudeDiff(radLongitude).pow(TWO) * sineLat(radLatitude) * cosLat(radLatitude) / TWO
                val deltaP4Term = (computeLongitudeDiff(radLongitude).pow(FOUR) * sineLat(radLatitude) * cosLat(radLatitude).pow(THREE) / 24.0) *
                        (FIVE - tanLat(radLatitude).pow(TWO) + (9.0 * auxNuSd) )
                return latitudeDiff + (radiusOfCurvature * (deltaP2Term + deltaP4Term))
            }

            private fun computeLongitudeDiff(radLongitude: Double, zoneRef:Double = ZN36L_LONGITUDE_REF):Double{
                return radLongitude - toRadians(zoneRef)
            }

            private fun computeRadiusOfCurvature(radLatitude: Double): Double {
                return SEMI_MAJOR /
                        (sqrt((ONE - ((ECCENTRICITY.pow(TWO)) * (sineLat(radLatitude).pow(TWO))))))
            }

            private fun computeAuxVarNu(radLatitude: Double):Double{
                return (ECCENTRICITY.pow(TWO) / (ONE - ECCENTRICITY.pow(TWO))) * cosLat(radLatitude).pow(TWO)
            }
        }
    }

    private fun toRadians(angle:Double):Double{
        return ((angle/DEG_180) * PI)
    }

    inner class ProjectedPoint(){
        constructor(x:Double, y:Double):this(){
            this.x = x
            this.y = y
        }
        var x = 0.00
        var y = 0.00
    }

    private companion object GeodeticSpheroids{
        object WGS84{
            const val SEMI_MAJOR = 6378135.00
            const val SEMI_MINOR = 6356752.00
            const val ECCENTRICITY = 0.0818191908426215
            const val FLATTENING = (ONE/298.257223563)
        }
    }

    private object CRSReferences{
        object EquatorialMercator{
            const val LONGITUDE_REF = 0.00
            const val LATITUDE_REF = 0.00
        }

        object UniversalTransverseMercator{
            object CentralMeridians {
                const val ZN01L_LONGITUDE_REF = -177.00
                const val ZN36L_LONGITUDE_REF = 33.00
            }

            object FalseOrigins{
                const val FALSE_EASTING = 500000.00
                const val FALSE_NORTHING_S = 10000000.00
                const val FALSE_NORTHING_N = 0.0
            }

            object ScaleFactors{
                const val MAX_SCALE = 0.9996
            }

            object EccentricityTerms{
                val TERM_0 = ONE -
                        (ECCENTRICITY.pow(TWO)/FOUR) -
                        ((THREE/64.0) * ECCENTRICITY.pow(FOUR)) -
                        ((FIVE/256.0) * ECCENTRICITY.pow(6))

                val TERM_2 = ((THREE/8.0) * ECCENTRICITY.pow(TWO)) +
                        ((THREE/32.0) * ECCENTRICITY.pow(FOUR)) +
                        ((45.0/1024.0) * ECCENTRICITY.pow(6))
                val TERM_4 = ((15.0/256.0) * ECCENTRICITY.pow(FOUR)) +
                        ((45.0/1024.0) * ECCENTRICITY.pow(6))
                val TERM_6 = ((35.0/3072.0) * ECCENTRICITY.pow(6))
            }
        }
    }

    private object KotProjConstants{
        const val MITRE = PI/4
        const val ONE = 1.0
        const val TWO = 2.0
        const val THREE = 3.0
        const val FOUR = 4.0
        const val FIVE = 5.0
        const val DEG_180 = 180.0
    }
}