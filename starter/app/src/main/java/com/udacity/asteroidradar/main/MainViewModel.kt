package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.enums.AsteroidFilter
import com.udacity.asteroidradar.repository.AsteroidsRadarRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val db = getDatabase(application)
    private val repository = AsteroidsRadarRepository(db)

    private val _navigationToDetail = MutableLiveData<Asteroid?>(null)
    val navigationToDetail: LiveData<Asteroid?>
        get() = _navigationToDetail

    val imageOfTheDay = repository.imgOfTheDay

    init {
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()
                repository.refreshImageOfTheDay()
            } catch (e: Exception) {
                Log.i("refreshDataErr", e.message.toString())
            }
        }
    }

    fun goToDetailFragment(asteroid: Asteroid) {
        _navigationToDetail.value = asteroid
    }

    fun goneToDetailFragment() {
        _navigationToDetail.value = null
    }

    fun getAsteroidsByFilter(filter: AsteroidFilter): LiveData<List<Asteroid>> {
        return when (filter) {
            AsteroidFilter.TODAY -> repository.getTodayAsteroids()
            AsteroidFilter.WEEK -> repository.getWeekAsteroids()
            else -> repository.getAllAsteroids()
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}