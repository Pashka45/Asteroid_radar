package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidsDao {
    @Query("SELECT * FROM dbasteroid ")
    fun getAll(): LiveData<List<DBAsteroid>>

    @Query("SELECT * FROM dbasteroid WHERE closeApproachDate = :date ORDER BY closeApproachDate ASC")
    fun getByDate(date: String): LiveData<List<DBAsteroid>>

    @Query("SELECT * FROM dbasteroid WHERE closeApproachDate >= :dateStart AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getByStartNEndDates(dateStart: String, endDate: String): LiveData<List<DBAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: ArrayList<DBAsteroid>)

    @Query("DELETE FROM dbasteroid WHERE closeApproachDate < DATE('now')")
    fun deleteAsteroidsPreviousDare()
}

@Dao
interface ImageOfTheDayDao {
    @Query("SELECT * FROM dbimageoftheday ORDER BY id DESC LIMIT 1")
    fun getCurrentImage(): LiveData<DBImageOfTheDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: DBImageOfTheDay)
}

@Database(entities = [DBAsteroid::class, DBImageOfTheDay::class], version = 3)
abstract class AsteroidsRadarDatabase : RoomDatabase() {
    abstract val asteroidsDao: AsteroidsDao
    abstract val imageOfTheDayDao: ImageOfTheDayDao

    companion object {
        const val dbname = "asteroids_radar"
    }
}

private lateinit var INSTANCE: AsteroidsRadarDatabase

fun getDatabase(context: Context): AsteroidsRadarDatabase {
    synchronized(AsteroidsRadarDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                    .databaseBuilder(context.applicationContext, AsteroidsRadarDatabase::class.java, AsteroidsRadarDatabase.dbname)
                    .build()
        }
    }
    return INSTANCE
}

/*val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE db MODIFY COLUMN pub_year INTEGE")
    }
}*/
