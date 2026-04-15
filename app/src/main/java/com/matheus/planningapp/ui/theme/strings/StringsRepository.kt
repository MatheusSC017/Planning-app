package com.matheus.planningapp.ui.theme.strings

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
    val searchCommitmentField: String

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
