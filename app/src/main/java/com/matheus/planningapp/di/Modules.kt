package com.matheus.planningapp.di

import androidx.room.Room
import com.matheus.planningapp.data.CalendarDatabase
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.calendar.CalendarRepositoryImpl
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.commitment.CommitmentRepositoryImpl
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.calendar.CalendarMenuViewModel
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
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
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CalendarMenuViewModel(get()) }
    viewModel { (commitmentFormMode: CommitmentFormMode) ->
        CommitmentFormViewModel(
            commitmentFormMode = commitmentFormMode,
            commitmentRepository = get()
        )
    }
}
