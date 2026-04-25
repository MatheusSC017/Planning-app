package com.matheus.planningapp.ui.theme.strings

class StringsRepositoryEnglish : StringsRepository {
    // Project information
    override val projectName = "Planning your life"
    override val timeline = "Timeline"

    // Text for buttons in general
    override val insertButton = "Insert new item"
    override val viewButton = "View"
    override val reminderButton = "Reminder"
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
    override val calendarEmptyNameError = "Calendar name cannot be empty"
    override val calendarNameMustBeUnique = "Calendar name must be unique"
    override val defaultCalendarCannotBeChanged = "The default calendar cannot be changed"
    override val defaultCalendarCannotBeDeleted = "The default calendar cannot be deleted"

    // Commitment information
    override val dialogDeleteCommitmentTitle = "Delete commitment"
    override val dialogDeleteCommitmentMessage = "Are you sure you want to delete this commitment?"
    override val commitmentTitleField = "Title"
    override val commitmentDescriptionField = "Description"
    override val commitmentStartField = "Start time"
    override val commitmentEndField = "End time"
    override val commitmentPriorityField = "Priority"
    override val searchCommitmentField = "Search for task"
    override val commitmentNotFoundError = "Commitment not found"
    override val commitmentStartTimeError = "Start time must be lesser than end time"
    override val commitmentTitleEmptyError = "Title cannot be empty"
    override val commitmentConflictError = "There is a conflict with other commitments"

    // Reminder information
    override val reminderInfo = "Reminder in %s Minutes"
    override val reminderField = "Minutes before the appointment"
    override val pastReminderError = "Past commitments cannot have reminders"

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
    override val monthNames =
        listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )
    override val weekDaysAbbrev = listOf('S', 'M', 'T', 'W', 'T', 'F', 'S')
    override val dateFormat = "%04d-%02d-%02d"
    override val hourFormat = "%02d:%02d"
}
