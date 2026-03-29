package com.matheus.planningapp.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

interface StringsRepository {
    // Project information
    val projectName: String
    val timeline: String

    // Text for buttons in general
    val insertButton: String
    val viewButton: String
    val dismissButton: String
    val confirmButton: String
    val cancelButton: String
    val deleteButton: String
    val updateButton: String
    val increaseButton: String
    val decreaseButton: String

    // Text for messages in general
    val savedMessage: String

    // Menu Options
    val menuButton: String
    val homeMenuButton: String
    val calendarsMenuButton: String
    val recurrencesMenuButton: String
    val settingsMenuButton: String
    val backMenuButton: String

    // View Options
    val columnView: String
    val gridView: String

    // Calendar information
    val dialogDeleteCalendarTitle: String
    val dialogDeleteCalendarMessage: String
    val calendarNameField: String
    val calendarIsDefaultField: String

    // Commitment information
    val dialogDeleteCommitmentTitle: String
    val dialogDeleteCommitmentMessage: String
    val commitmentTitleField: String
    val commitmentDescriptionField: String
    val commitmentStartField: String
    val commitmentEndField: String
    val commitmentPriorityField: String

    // Recurrence information
    val recurrenceFrequencyField: String
    val recurrenceIntervalField: String
    val recurrenceDayOfMonthField: String
    val recurrenceWeekDaysField: String
    val recurrenceIsRecurrenceActiveField: String
    val recurrenceValueField: String

    // Settings information
    val dialogUpdateSettingTitle: String
    val dialogUpdateSettingMessage: String
    val settingViewModeField: String
    val settingNotificationField: String

    // Datetime information
    val monthNames: List<String>
    val weekDaysAbbrev: List<Char>
    val dateFormat: String
    val hourFormat: String
}

class StringsRepositoryEnglish: StringsRepository {
    // Project information
    override val projectName = "Planning your life"
    override val timeline = "Timeline"

    // Text for buttons in general
    override val insertButton = "Insert new item"
    override val viewButton = "View"
    override val dismissButton = "Dismiss"
    override val confirmButton = "Confirm"
    override val cancelButton = "Cancel"
    override val deleteButton = "Delete"
    override val updateButton = "Update"
    override val increaseButton = "Increase"
    override val decreaseButton = "Decrease"

    // Text for messages in general
    override val savedMessage = "Saved"

    // Menu Options
    override val menuButton = "Menu"
    override val homeMenuButton = "Home"
    override val calendarsMenuButton = "Calendars"
    override val recurrencesMenuButton = "Recurrences"
    override val settingsMenuButton = "Settings"
    override val backMenuButton = "Back"

    // View Options
    override val columnView = "Column"
    override val gridView = "Grid"

    // Calendar information
    override val dialogDeleteCalendarTitle = "Delete calendar"
    override val dialogDeleteCalendarMessage = "Are you sure you want to delete this calendar?"
    override val calendarNameField = "Calendar name"
    override val calendarIsDefaultField = "Set as default"

    // Commitment information
    override val dialogDeleteCommitmentTitle = "Delete commitment"
    override val dialogDeleteCommitmentMessage = "Are you sure you want to delete this commitment?"
    override val commitmentTitleField = "Title"
    override val commitmentDescriptionField = "Description"
    override val commitmentStartField = "Start time"
    override val commitmentEndField = "End time"
    override val commitmentPriorityField = "Priority"

    // Recurrence information
    override val recurrenceFrequencyField = "Frequency"
    override val recurrenceIntervalField = "Interval"
    override val recurrenceDayOfMonthField = "Day of month"
    override val recurrenceWeekDaysField = "Week days"
    override val recurrenceIsRecurrenceActiveField = "Is this a recurring task?"
    override val recurrenceValueField = "Value"

    // Settings information
    override val dialogUpdateSettingTitle = "Confirm the settings"
    override val dialogUpdateSettingMessage = "Are you sure you want to save the settings"
    override val settingViewModeField = "Viewing mode"
    override val settingNotificationField = "Notification configuration"

    // Datetime information
    override val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    override val weekDaysAbbrev = listOf('S', 'M', 'T', 'W', 'T', 'F', 'S')
    override val dateFormat = "%04d-%02d-%02d"
    override val hourFormat = "%02d:%02d"
}

val LocalStrings = staticCompositionLocalOf<StringsRepository> {
    error("No Strings provided")
}
