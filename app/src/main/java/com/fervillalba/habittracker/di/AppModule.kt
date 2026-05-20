package com.fervillalba.habittracker.di

import android.content.Context
import androidx.room.Room
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.data.local.HabitDao
import com.fervillalba.habittracker.data.local.HabitDatabase
import com.fervillalba.habittracker.data.repository.HabitRepositoryImpl
import com.fervillalba.habittracker.domain.repository.HabitRepository
import com.fervillalba.habittracker.util.DispatchersProvider
import com.fervillalba.habittracker.util.StandardDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHabitDatabase(
        @ApplicationContext context: Context
    ): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .enableMultiInstanceInvalidation()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao): HabitRepository {
        return HabitRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider {
        return StandardDispatchers()
    }
}
