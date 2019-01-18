package com.sergiocruz.nanogram.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sergiocruz.nanogram.database.typeconverters.CaptionTypeConverter
import com.sergiocruz.nanogram.database.typeconverters.CommentsTypeConverter
import com.sergiocruz.nanogram.database.typeconverters.ImagesTypeConverter
import com.sergiocruz.nanogram.database.typeconverters.LikesTypeConverter
import com.sergiocruz.nanogram.model.ImageVar

@Database(entities = [ImageVar::class], version = 1, exportSchema = false)
@TypeConverters(
    ImagesTypeConverter::class,
    LikesTypeConverter::class,
    CaptionTypeConverter::class,
    CommentsTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun databaseDao(): DatabaseDao

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private var DATABASE_INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (DATABASE_INSTANCE == null) {
                DATABASE_INSTANCE = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    //.allowMainThreadQueries() // sync
                    //.fallbackToDestructiveMigration() // Destroys the DB and recreates it with the new schema
                    .build()
            }
            return DATABASE_INSTANCE as AppDatabase
        }

    }
}
