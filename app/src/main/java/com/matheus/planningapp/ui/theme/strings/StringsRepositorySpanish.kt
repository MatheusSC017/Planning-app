package com.matheus.planningapp.ui.theme.strings

class StringsRepositorySpanish : StringsRepository {
    // Project information
    override val projectName = "Planificando tu vida"
    override val timeline = "Cronograma"

    // Text for buttons in general
    override val insertButton = "Insertar nuevo elemento"
    override val viewButton = "Ver"
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

    // Commitment information
    override val dialogDeleteCommitmentTitle = "Eliminar compromiso"
    override val dialogDeleteCommitmentMessage = "¿Estás seguro de que deseas eliminar este compromiso?"
    override val commitmentTitleField = "Título"
    override val commitmentDescriptionField = "Descripción"
    override val commitmentStartField = "Hora de inicio"
    override val commitmentEndField = "Hora de finalización"
    override val commitmentPriorityField = "Prioridad"

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
