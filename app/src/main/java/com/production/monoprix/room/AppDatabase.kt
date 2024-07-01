package com.production.monoprix.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.production.monoprix.model.ProductByCodeModel
import com.production.monoprix.model.ShoppingListDataModel

@Database(
    entities = [ProductByCodeModel::class,ShoppingListDataModel::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun todoDao(): ProductsDao
    abstract fun shoppingDao(): ShoppingLocalDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "monoprix.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    }
}