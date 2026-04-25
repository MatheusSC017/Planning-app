package com.matheus.planningapp.di

import androidx.room.Room
import com.matheus.planningapp.data.CalendarDatabase
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.calendar.CalendarRepositoryImpl
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.commitment.CommitmentRepositoryImpl
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import com.matheus.planningapp.data.recurrence.RecurrenceRepositoryImpl
import com.matheus.planningapp.data.reminder.ReminderRepository
import com.matheus.planningapp.data.reminder.ReminderRepositoryImpl
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryEnglish
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryPortuguese
import com.matheus.planningapp.ui.theme.strings.StringsRepositorySpanish
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.viewmodel.calendar.CalendarViewModel
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import com.matheus.planningapp.viewmodel.setting.SettingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.Locale

val appModules =
    module {
        single<StringsRepository> {
            when (Locale.getDefault().language) {
                "pt" -> StringsRepositoryPortuguese()
                "es" -> StringsRepositorySpanish()
                else -> StringsRepositoryEnglish()
            }
        }

        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    CalendarDatabase::class.java,
                    "calendar-database",
                ).fallbackToDestructiveMigration(false)
                .build()
        }

        single { get<CalendarDatabase>().calendarDao() }
        single { get<CalendarDatabase>().commitmentDao() }
        single { get<CalendarDatabase>().recurrenceDao() }
        single { get<CalendarDatabase>().reminderDao() }

        single<CalendarRepository> { CalendarRepositoryImpl(get()) }
        single<CommitmentRepository> { CommitmentRepositoryImpl(get()) }
        single<RecurrenceRepository> { RecurrenceRepositoryImpl(get()) }
        single<ReminderRepository> { ReminderRepositoryImpl(get()) }
        single<SettingsRepository> { SettingsRepository(get()) }
        single<TaskNotificationScheduler> { TaskNotificationScheduler(get(), get()) }

        viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { CalendarViewModel(get(), get()) }
        viewModel { (commitmentFormMode: CommitmentFormMode) ->
            CommitmentFormViewModel(
                commitmentFormMode = commitmentFormMode,
                commitmentRepository = get(),
                settingsRepository = get(),
                recurrenceRepository = get(),
                taskNotificationScheduler = get(),
                strings = get(),
            )
        }
        viewModel { SettingViewModel(get(), get(), get(), get()) }
        viewModel { RecurrenceViewModel(get(), get()) }
    }
