package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.ImageOfTheDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsRadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRadarRepository(private val db: AsteroidsRadarDatabase) {

    private val daysAfter = 7
    private val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)

    val imgOfTheDay: LiveData<ImageOfTheDay> =
        Transformations.map(db.imageOfTheDayDao.getCurrentImage()) {
            it?.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidsStr = Network.nasaService.getAsteroidsList(
                sdf.format(Date()),
                sdf.format(getDaysAfter(daysAfter)),
                Constants.NASA_API_KEY
            ).await()

            db.asteroidsDao.insertAll(parseAsteroidsJsonResult(JSONObject(asteroidsStr)))
        }
    }

    suspend fun removePreviousDateAsteroids() {
        withContext(Dispatchers.IO) {
            db.asteroidsDao.deleteAsteroidsPreviousDare()
        }
    }

    suspend fun refreshImageOfTheDay() {
        withContext(Dispatchers.IO) {
            val image = Network.nasaService.getImageOfTheDay(Constants.NASA_API_KEY).await()

            db.imageOfTheDayDao.insert(image.asDatabaseModel())
        }
    }

    private fun getDaysAfter(daysAfter: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAfter)

        return calendar.time
    }

    fun getAllAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(db.asteroidsDao.getAll()) {
            it.asDomainModel()
        }
    }

    fun getTodayAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(db.asteroidsDao.getByDate(sdf.format(Date()))) {
            it?.asDomainModel()
        }
    }

    fun getWeekAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(db.asteroidsDao.getByStartNEndDates(sdf.format(Date()), sdf.format(getDaysAfter(7)))) {
            it?.asDomainModel()
        }
    }
}
