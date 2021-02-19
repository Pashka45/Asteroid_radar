package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DBAsteroid
import com.udacity.asteroidradar.database.DBImageOfTheDay

//@JsonClass(generateAdapter = true)
/*data class NetAsteroidsContainer(
       @Json(name = "near_earth_objects") val nearEarthObjects: Map<String, List<NetAsteroid>>
)*/

@JsonClass(generateAdapter = true)
data class NetAsteroid(
        val id: Long,
        val codename: String,
        val closeApproachDate: String,
        val absoluteMagnitude: Double,
        val estimatedDiameter: Double,
        val relativeVelocity: Double,
        val distanceFromEarth: Double,
        val isPotentiallyHazardous: Boolean
)

@JsonClass(generateAdapter = true)
data class NetImageOfTheDay(
        //val id: Long,
        val url: String,
        @Json(name = "media_type") val mediaType: String,
        val title: String
)

/*fun NetAsteroidsContainer.asDatabaseModel(): Array<DBAsteroid> {
    return nearEarthObjects.map {
        DBAsteroid (
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous
                )
    }.toTypedArray()
}*/

fun NetImageOfTheDay.asDatabaseModel(): DBImageOfTheDay {
    return DBImageOfTheDay(
            id = 0,
            mediaType = mediaType,
            url = url,
            title = title
    )
}
