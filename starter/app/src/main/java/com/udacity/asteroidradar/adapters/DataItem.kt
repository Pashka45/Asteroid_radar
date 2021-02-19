package com.udacity.asteroidradar.adapters

import com.udacity.asteroidradar.Asteroid

sealed class DataItem {
    data class AsteroidItem(val asteroid: Asteroid): DataItem() {
        override val id = asteroid.id
    }

    abstract val id: Long
}