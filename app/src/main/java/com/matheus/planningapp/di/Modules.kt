package com.matheus.planningapp.di

import androidx.room.Room
import com.matheus.planningapp.data.CalendarDatabase
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.calendar.CalendarRepositoryImpl
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.commitment.CommitmentRepositoryImpl
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import com.matheus.planningapp.data.recurrence.RecurrenceRepositoryImpl
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.calendar.CalendarViewModel
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import com.matheus.planningapp.viewmodel.setting.SettingViewModel
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
    single { get<CalendarDatabase>().recurrenceDao() }

    single<CalendarRepository> { CalendarRepositoryImpl(get())}
    single<CommitmentRepository> { CommitmentRepositoryImpl(get())}
    single<RecurrenceRepository> { RecurrenceRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepository(get()) }
    single<TaskNotificationScheduler> { TaskNotificationScheduler(get(), get()) }

    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { (commitmentFormMode: CommitmentFormMode) ->
        CommitmentFormViewModel(
            commitmentFormMode = commitmentFormMode,
            commitmentRepository = get(),
            settingsRepository = get(),
            recurrenceRepository = get(),
            taskNotificationScheduler = get()
        )
    }
    viewModel { SettingViewModel(get(), get(), get(), get()) }
    viewModel { RecurrenceViewModel(get(), get()) }
}
