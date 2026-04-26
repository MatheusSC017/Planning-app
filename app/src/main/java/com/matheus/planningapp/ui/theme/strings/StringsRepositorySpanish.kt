package com.matheus.planningapp.ui.theme.strings

class StringsRepositorySpanish : StringsRepository {
    // Project information
    override val projectName = "Planificando tu vida"
    override val timeline = "Cronograma"

    // Text for buttons in general
    override val insertButton = "Insertar"
    override val viewButton = "Ver"
    override val reminderButton = "Recordar"
    override val dismissButton = "Descartar"
    override val confirmButton = "Confirmar"
    override val cancelButton = "Cancelar"
    override val deleteButton = "Eliminar"
    override val updateButton = "Actualizar"
    override val increaseButton = "Incrementar"
    override val decreaseButton = "Disminuir"

    // Text for messages in general
    override val savedMessage = "Guardado"

    // Menu Options
    override val menuButton = "Menú"
    override val homeMenuButton = "Inicio"
    override val calendarsMenuButton = "Calendarios"
    override val recurrencesMenuButton = "Recurrencias"
    override val settingsMenuButton = "Configuración"
    override val backMenuButton = "Atrás"

    // View Options
    override val columnView = "Columna"
    override val gridView = "Cuadrícula"

    // Calendar information
    override val dialogDeleteCalendarTitle = "Eliminar calendario"
    override val dialogDeleteCalendarMessage = "¿Estás seguro de que deseas eliminar este calendario?"
    override val calendarNameField = "Nombre del calendario"
    override val calendarIsDefaultField = "Establecer como predeterminado"
    override val calendarEmptyNameError = "El nombre del calendario no puede estar vacío"
    override val calendarNameMustBeUnique = "El nombre del calendario debe ser único"
    override val defaultCalendarCannotBeChanged = "El calendario predeterminado no puede ser cambiado"
    override val defaultCalendarCannotBeDeleted = "El calendario predeterminado no puede ser eliminado"

    // Commitment information
    override val dialogDeleteCommitmentTitle = "Eliminar compromiso"
    override val dialogDeleteCommitmentMessage = "¿Estás seguro de que deseas eliminar este compromiso?"
    override val commitmentTitleField = "Título"
    override val commitmentDescriptionField = "Descripción"
    override val commitmentStartField = "Hora de inicio"
    override val commitmentEndField = "Hora de finalización"
    override val commitmentPriorityField = "Prioridad"
    override val searchCommitmentField = "Buscar tarea"
    override val commitmentNotFoundError = "Compromiso no encontrado"
    override val commitmentStartTimeError = "La hora de inicio debe ser menor que la hora de finalización"
    override val commitmentTitleEmptyError = "El título no puede estar vacío"
    override val commitmentConflictError = "Hay un conflicto con otro compromiso"

    // Reminder information
    override val reminderInfo = "Recordar en %s minutos"
    override val reminderField = "Minutos antes del compromiso"
    override val pastReminderError = "Los compartimentos en el pasado no pueden tener recordatorios"

    // Recurrence information
    override val recurrenceFrequencyField = "Frecuencia"
    override val recurrenceIntervalField = "Intervalo"
    override val recurrenceDayOfMonthField = "Día del mes"
    override val recurrenceWeekDaysField = "Días de la semana"
    override val recurrenceIsRecurrenceActiveField = "¿Es una tarea recurrente?"
    override val recurrenceValueField = "Valor"

    // Settings information
    override val dialogUpdateSettingTitle = "Confirmar configuración"
    override val dialogUpdateSettingMessage = "¿Estás seguro de que deseas guardar la configuración?"
    override val settingViewModeField = "Modo de visualización"
    override val settingNotificationField = "Configuración de notificaciones"

    // Datetime information
    override val monthNames =
        listOf(
            "Enero",
            "Febrero",
            "Marzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre",
        )
    override val weekDaysAbbrev = listOf('D', 'L', 'M', 'M', 'J', 'V', 'S')
    override val dateFormat = "%04d-%02d-%02d"
    override val hourFormat = "%02d:%02d"
}
