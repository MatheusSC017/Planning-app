package com.matheus.planningapp.ui.theme.strings

class StringsRepositoryEnglish : StringsRepository {
    // Project information
    override val projectName = "Planning your life"
    override val timeline = "Timeline"

    // Text for buttons in general
    override val insertButton = "Insert"
    override val viewButton = "View"
    override val reminderButton = "Reminder"
    override val dismissButton = "Dismiss"
    override val confirmButton = "Confirm"
    override val cancelButton = "Cancel"
    override val deleteButton = "Delete"
    override val updateButton = "Update"
    override val increaseButton = "Increase"
    override val decreaseButton = "Decrease"
    override val nextButton = "Next"
    override val previousButton = "Previous"

    // Text for messages in general
    override val savedMessage = "Saved"
    override val noValuesFound = "No values found"

    // Menu Options
    override val menuButton = "Menu"
    override val homeMenuButton = "Home"
    override val calendarsMenuButton = "Calendars"
    override val recurrencesMenuButton = "Recurrences"
    override val settingsMenuButton = "Settings"
    override val backMenuButton = "Back"
    override val focusModeMenuButton = "Focus Mode"

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
    override val reminderListTitle = "Active Reminders"
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

    // Category information
    override val categoryManagementTitle = "Categories"
    override val categoryNameField = "Name"
    override val categoryDescriptionField = "Description"
    override val searchCategoryField = "Search"
    override val noCategorySelected = "No category selected"
    override val createCategoryTitle = "Create New Category"
    override val updateCategoryTitle = "Update Category"
    override val categoryEmptyNameError = "Category name cannot be empty"
    override val categoryNotFoundError = "Category not found"
    override val categoryInsertError = "Error creating category"
    override val categoryUpdateError = "Error updating category"
    override val categoryDeleteError = "Error deleting category"
    override val noCategoriesFound = "No categories found"
    override val categoryDeletedMessage = "Category deleted successfully"
    override val editButton = "Edit"
    override val searchError = "Error searching categories"

    // Focus mode information
    override val startFocusModeButton = "Start Focus Mode"
    override val stopFocusModeButton = "Stop Focus Mode"
    override val pauseFocusModeButton = "Pause Focus Mode"
    override val focusHistoryTitle = "Focus History"
    override val focusHistoryDuration = "Duration: %d min"
    override val focusHistoryCompleted = "Completed"
    override val focusHistoryIncomplete = "Incomplete"
    override val motivationalQuotes = listOf(
        "Focus on being productive instead of busy.",
        "Your life is the result of your choices.",
        "Energy flows where attention goes.",
        "The secret of getting ahead is getting started.",
        "Don't stop until you're proud.",
        "Simplicity is the ultimate sophistication.",
        "Do what you can, with what you have, where you are.",
        "Success is the sum of small efforts repeated day in and day out."
    )
    override val deepFocusLabel = "Deep Focus"
    override val deepFocusDescription = "Enable Do Not Disturb automatically"
    override val dndPermissionRequired = "Do Not Disturb permission is required for Deep Focus mode."
    override val appTrackingLabel = "App Tracking"
    override val appTrackingDescription = "Nudge you if you open distracting apps"
    override val usageStatsPermissionRequired = "Usage Stats permission is required to track distracting apps."
    override val distractingAppNudgeTitle = "Stay Focused!"
    override val distractingAppNudgeMessage = "You opened a distracting app. Get back to work!"

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
    override val dateFormat = "yyyy-MM-dd"
    override val hourFormat = "%02d:%02d"
}
