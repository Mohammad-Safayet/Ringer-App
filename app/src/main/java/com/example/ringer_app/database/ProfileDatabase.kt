package com.example.ringer_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ringer_app.repository.model.UserProfile
import com.example.ringer_app.utils.Constants.PROFILE_ENTITY_NAME

@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract class ProfileDatabase: RoomDatabase() {

    abstract fun getProfileDao(): ProfileDao

    companion object {
        @Volatile
        private  var INSTANCE: ProfileDatabase? = null

        fun getDatabase(context: Context): ProfileDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileDatabase::class.java,
                    PROFILE_ENTITY_NAME,
                ).build()

                INSTANCE = instance

                return  instance
            }
        }
    }
}