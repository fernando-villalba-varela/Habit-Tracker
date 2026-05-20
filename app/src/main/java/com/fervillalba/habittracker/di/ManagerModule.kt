package com.fervillalba.habittracker.di

import com.fervillalba.habittracker.data.manager.WorkManagerReminderManager
import com.fervillalba.habittracker.data.manager.WorkManagerWidgetManager
import com.fervillalba.habittracker.domain.manager.ReminderManager
import com.fervillalba.habittracker.domain.manager.WidgetManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindReminderManager(
        workManagerReminderManager: WorkManagerReminderManager
    ): ReminderManager

    @Binds
    @Singleton
    abstract fun bindWidgetManager(
        workManagerWidgetManager: WorkManagerWidgetManager
    ): WidgetManager
}
