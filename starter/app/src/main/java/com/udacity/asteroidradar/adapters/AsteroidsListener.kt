package com.udacity.asteroidradar.adapters

import com.udacity.asteroidradar.Asteroid

class AsteroidsListener(val block: (Asteroid) -> Unit) {

    fun onClick(asteroid: Asteroid) = block(asteroid)
}