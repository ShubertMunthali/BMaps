package com.infbyte.parsers

import com.infbyte.kotmap.BMapLayer
import com.infbyte.kotmap.BPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

class BGeoJSONParser {

    private val geoData = BGeoJSONObject()
    private val bGeoJSONHelper = BGeoJSONHelper()
    private val featureHelper = FeatureHelper()
    private val crsHelper = CRSHelper()
    private lateinit var jsonStreamReader: InputStreamReader
    private lateinit var bufferedStreamReader:BufferedReader
    private val bMapLayer = BMapLayer()

    fun createLayerFromFile(geoJSONFile:File?): BMapLayer {
        try{
            if(geoJSONFile != null){
                createReader(geoJSONFile)
                convertJSONFileStringToObject()
                bMapLayer.type = geoData.feature.geometry.type
                bMapLayer.name = geoData.feature.properties.district
                bMapLayer.coordinates.addAll(geoData.feature.geometry.coordinates)
                bufferedStreamReader.close()
            }
            else{
                println("Could not create layer, no com.infbyte.data file is available!")
            }
        }
        catch(e:FileNotFoundException){
            println("Could not find the file specified!")
        }
        return bMapLayer
    }

    private fun createReader(geoJSONFile: File?){
        jsonStreamReader = geoJSONFile?.reader()!!
        bufferedStreamReader = BufferedReader(jsonStreamReader)
    }

    private fun convertJSONFileStringToObject(){

        val jsonFileString = bufferedStreamReader.readText()

        println("${jsonFileString.first()} ${jsonFileString.last()}")

        val coordinatesString:String

        if(jsonFileString.first().toString() == OPEN_BRACE && jsonFileString.last().toString() == CLOSE_BRACE){

            /**
             * Reading feature type key and value from the main GeoJSON object
             * */
            bGeoJSONHelper.typeKey = jsonFileString.substringAfter(OPEN_BRACE).substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.featureType = jsonFileString.substringAfter(OPEN_BRACE).substringAfter(bGeoJSONHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA)

            /**
             * Reading the feature name key and value from the main GeoJSON object
             */
            bGeoJSONHelper.nameKey = jsonFileString.substringAfter(geoData.featureType + INVERTED_COMMA + COMMA).substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)
            geoData.featureName = jsonFileString.substringAfter(bGeoJSONHelper.nameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA)

            /**
             * Reading the Coordinate Reference System object key and value from the main GeoJSON object
             */
            bGeoJSONHelper.crsKey = jsonFileString.substringAfter(geoData.featureName + INVERTED_COMMA + COMMA).substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE)
            /**
             * Reading the Coordinate Reference System type key and value from the CRS object in the GeoJSON object
             */
            crsHelper.typeKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)
            geoData.crs.type = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA)
            /**
             * Reading the Coordinate Reference System properties key and value from the CRS object in the GeoJSON object
             */
            crsHelper.propertiesKey = jsonFileString.substringAfter( bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE)
            /**
             * Reading the properties name key and value from the properties object in the CRS object
             */
            crsHelper.properties.nameKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.crs.properties.name = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA)

            /**
             * Reading the features key and value from the main GeoJSON object
             */
            bGeoJSONHelper.featuresKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON)


            featureHelper.typeKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.feature.type = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA)

            featureHelper.propertiesKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + OPEN_BRACE)

            featureHelper.properties.taNameKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.feature.properties._TAName = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA)

            featureHelper.properties.districtKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA + SPACE +
                    INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.feature.properties.district = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA + SPACE +
                    INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA)

            featureHelper.properties.regionKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA + SPACE +
                    INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.feature.properties.region = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA)

            featureHelper.geometryKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.properties.region + INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + OPEN_BRACE)

            featureHelper.geometry.typeKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.properties.region + INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + featureHelper.geometryKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA)

            geoData.feature.geometry.type = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.properties.region + INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + featureHelper.geometryKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.geometry.typeKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COMMA)

            featureHelper.geometry.coordinatesKey = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.properties.region + INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + featureHelper.geometryKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.geometry.typeKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.geometry.type + INVERTED_COMMA + COMMA).
            substringAfter(INVERTED_COMMA).
            substringBefore(INVERTED_COMMA + COLON + SPACE)

            coordinatesString = jsonFileString.substringAfter(bGeoJSONHelper.crsKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE +
                    INVERTED_COMMA + crsHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.type + INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA
                    + crsHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + crsHelper.properties.nameKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.crs.properties.name + INVERTED_COMMA + SPACE + CLOSE_BRACE + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + bGeoJSONHelper.featuresKey + INVERTED_COMMA + COLON + SPACE + OPEN_SQUARE_BRACKET).
            substringAfter(OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.typeKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.type +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.propertiesKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA +
                    featureHelper.properties.taNameKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties._TAName + INVERTED_COMMA + COMMA +
                    SPACE + INVERTED_COMMA + featureHelper.properties.districtKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.properties.district +
                    INVERTED_COMMA + COMMA + SPACE + INVERTED_COMMA + featureHelper.properties.regionKey + INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA +
                    geoData.feature.properties.region + INVERTED_COMMA + SPACE + CLOSE_BRACE + COMMA).
            substringAfter(INVERTED_COMMA + featureHelper.geometryKey + INVERTED_COMMA + COLON + SPACE + OPEN_BRACE + SPACE + INVERTED_COMMA + featureHelper.geometry.typeKey +
                    INVERTED_COMMA + COLON + SPACE + INVERTED_COMMA + geoData.feature.geometry.type + INVERTED_COMMA + COMMA).
            substringAfter(INVERTED_COMMA + featureHelper.geometry.coordinatesKey + INVERTED_COMMA + COLON + SPACE).
            substringAfter(OPEN_SQUARE_BRACKET + SPACE + OPEN_SQUARE_BRACKET + SPACE).
            substringBefore(SPACE + CLOSE_SQUARE_BRACKET + SPACE + CLOSE_SQUARE_BRACKET + SPACE + CLOSE_BRACE)

            readCoordinatesFromString(coordinatesString)

            geoData.features.add(geoData.feature)

            bGeoJSONHelper.apply {
                println("$typeKey, $nameKey, $crsKey, $featuresKey")
            }
            crsHelper.apply{
                println("$typeKey, $propertiesKey:${properties.nameKey}")
            }
            featureHelper.apply{
                println("$typeKey, $propertiesKey:${properties.taNameKey} ${properties.districtKey} ${properties.regionKey}, " +
                        "$geometryKey: ${geometry.typeKey} ${geometry.coordinatesKey}")
            }
            println("\nParsing GeoJson...\n")
            println("Feature Type: " + geoData.featureType)
            println("Feature Name: " + geoData.featureName)
            println("Feature CRS: \n\t" +
                    "Type: ${geoData.crs.type}\n\t" +
                    "Properties: \n\t\t" +
                    "Name: ${geoData.crs.properties.name}")
            println("Feature:\n\t" +
                    "Type: ${geoData.feature.type}\n\t" +
                    "Properties:\n\t\t" +
                    "T/A: ${geoData.feature.properties._TAName}\n\t\t" +
                    "District: ${geoData.feature.properties.district}\n\t\t" +
                    "Region: ${geoData.feature.properties.region}\n\t" +
                    "Geometry:\n\t\t" +
                    "Type: ${geoData.feature.geometry.type}\n\t\t" +
                    "Coordinates:")
/*            for(coordinate in geoData.feature.geometry.coordinates){
                println("\t\t\t\t\t(${coordinate.x}, ${coordinate.y})")
            }*/
        }
        else{
            println("The file structure is unknown!")
        }
    }

    private fun readCoordinatesFromString(coordinates:String){
        var coordinatesPlusDelimiter = "$coordinates$COORD_SEPARATOR"
        var numberOfCoordinates = 0
        coordinates.forEach {
            if(it.toString() == OPEN_SQUARE_BRACKET){
                numberOfCoordinates++
            }
        }

        for(i in 1 .. numberOfCoordinates){
            val coordinate = coordinatesPlusDelimiter.substringBefore(COORD_SEPARATOR_1)
            val x = coordinate.substringAfter(OPEN_SQUARE_BRACKET + SPACE).
                    substringBefore(COORD_SEPARATOR).toDouble()
            val y = coordinate.substringAfter(COORD_SEPARATOR).toDouble()
            //println("$x, $y")
            val bPoint = BPoint(x, y)
            geoData.feature.geometry.coordinates.add(bPoint)
            coordinatesPlusDelimiter = coordinatesPlusDelimiter.removePrefix("$coordinate$COORD_SEPARATOR_1" )
        }
    }

    private class BGeoJSONObject{
        var featureType:String = ""
        var featureName:String = ""
        var crs:CRS = CRS()
        var feature = Feature()
        var features = mutableListOf<Feature>()

        class CRS{
            var type:String = ""
            var properties = Properties()
            class Properties{
                var name:String = ""
            }
        }

        class Feature{
            var type:String = ""
            val properties = Properties()
            val geometry = Geometry()
            class Properties{
                var _TAName:String = ""
                var district:String = ""
                var region:String = ""
            }
            class Geometry{
                var type:String = ""
                var coordinates  = mutableListOf<BPoint>()
            }
        }
    }

    private class BGeoJSONHelper{
        var typeKey = ""
        var nameKey = ""
        var crsKey = ""
        var featuresKey = ""
    }

    private class CRSHelper{
        var typeKey:String = ""
        var propertiesKey:String = ""
        val properties = Properties()
        class Properties{
            var nameKey:String = ""
        }
    }

    private class FeatureHelper{
        var typeKey = ""
        var propertiesKey = ""
        var geometryKey = ""
        val properties = Properties()
        val geometry = Geometry()
        class Properties{
            var taNameKey = ""
            var districtKey = ""
            var regionKey = ""
        }
        class Geometry{
            var typeKey = ""
            var coordinatesKey = ""
        }
    }

    companion object Delimiters{
        const val OPEN_BRACE = "{"
        const val CLOSE_BRACE = "}"
        const val INVERTED_COMMA = "\""
        const val COLON = ":"
        const val OPEN_SQUARE_BRACKET = "["
        const val CLOSE_SQUARE_BRACKET = "]"
        const val COMMA = ","
        const val SPACE = " "
        const val NEWLINE = "\n"
        const val COORD_SEPARATOR = ", "
        const val COORD_SEPARATOR_1 = SPACE + CLOSE_SQUARE_BRACKET + COMMA + SPACE
    }

}