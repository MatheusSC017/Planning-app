package com.matheus.planningapp.ui.theme.strings

interface StringsRepository {
    // Project information
    val projectName: String
    val timeline: String

    // Text for buttons in general
    val insertButton: String
    val viewButton: String
    val reminderButton: String
    val dismissButton: String
    val confirmButton: String
    val cancelButton: String
    val deleteButton: String
    val updateButton: String
    val increaseButton: String
    val decreaseButton: String

    // Text for messages in general
    val savedMessage: String
    val noValuesFound: String

    // Menu Options
    val menuButton: String
    val homeMenuButton: String
    val calendarsMenuButton: String
    val recurrencesMenuButton: String
    val settingsMenuButton: String
    val backMenuButton: String
    val focusModeMenuButton: String

    // View Options
    val columnView: String
    val gridView: String

    // Calendar information
    val dialogDeleteCalendarTitle: String
    val dialogDeleteCalendarMessage: String
    val calendarNameField: String
    val calendarIsDefaultField: String
    val calendarEmptyNameError: String
    val calendarNameMustBeUnique: String
    val defaultCalendarCannotBeChanged: String
    val defaultCalendarCannotBeDeleted: String

    // Commitment information
    val dialogDeleteCommitmentTitle: String
    val dialogDeleteCommitmentMessage: String
    val commitmentTitleField: String
    val commitmentDescriptionField: String
    val commitmentStartField: String
    val commitmentEndField: String
    val commitmentPriorityField: String
    val searchCommitmentField: String
    val commitmentNotFoundError: String
    val commitmentStartTimeError: String
    val commitmentTitleEmptyError: String
    val commitmentConflictError: String

    // Reminder information
    val reminderListTitle: String
    val reminderInfo: String
    val reminderField: String
    val pastReminderError: String

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

    // Category information
    val categoryManagementTitle: String
    val categoryNameField: String
    val categoryDescriptionField: String
    val searchCategoryField: String
    val noCategorySelected: String
    val createCategoryTitle: String
    val updateCategoryTitle: String
    val categoryEmptyNameError: String
    val categoryNotFoundError: String
    val categoryInsertError: String
    val categoryUpdateError: String
    val categoryDeleteError: String
    val noCategoriesFound: String
    val categoryDeletedMessage: String
    val editButton: String
    val searchError: String

    // Focus mode information
    val stopFocusModeButton: String
    val dialogStopFocusTitle: String
    val dialogStopFocusMessage: String

    // Datetime information
    val monthNames: List<String>
    val weekDaysAbbrev: List<Char>
    val dateFormat: String
    val hourFormat: String
}
