package com.matheus.planningapp.di

import androidx.room.Room
import com.matheus.planningapp.data.CalendarDatabase
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.data.CalendarRepositoryImpl
import com.matheus.planningapp.data.CommitmentRepository
import com.matheus.planningapp.data.CommitmentRepositoryImpl
import com.matheus.planningapp.viewmodel.CalendarViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CalendarDatabase::class.java,
            "calendar-database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<CalendarDatabase>().calendarDao() }
    single { get<CalendarDatabase>().commitmentDao() }
    single<CalendarRepository> { CalendarRepositoryImpl(get())}
    single<CommitmentRepository> { CommitmentRepositoryImpl(get())}
    single { CalendarViewModel(get(), get()) }
}
