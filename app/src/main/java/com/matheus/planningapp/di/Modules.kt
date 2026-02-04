package com.matheus.planningapp.di

import androidx.room.Room
import com.matheus.planningapp.data.CalendarDatabase
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.data.CalendarRepositoryImpl
import com.matheus.planningapp.data.CommitmentRepository
import com.matheus.planningapp.data.CommitmentRepositoryImpl
import com.matheus.planningapp.viewmodel.CommitmentFormMode
import com.matheus.planningapp.viewmodel.CalendarMenuViewModel
import com.matheus.planningapp.viewmodel.CalendarViewModel
import com.matheus.planningapp.viewmodel.CommitmentFormViewModel
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
    single { CalendarMenuViewModel(get()) }
    single { (commitmentFormMode: CommitmentFormMode) ->
        CommitmentFormViewModel(
            commitmentFormMode = commitmentFormMode,
            commitmentRepository =get()
        )
    }
}
