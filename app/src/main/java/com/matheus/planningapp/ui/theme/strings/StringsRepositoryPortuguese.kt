package com.matheus.planningapp.ui.theme.strings

class StringsRepositoryPortuguese : StringsRepository {
    // Project information
    override val projectName = "Planejando sua vida"
    override val timeline = "Cronograma"

    // Text for buttons in general
    override val insertButton = "Inserir"
    override val viewButton = "Visualizar"
    override val reminderButton = "Lembrete"
    override val dismissButton = "Dispensar"
    override val confirmButton = "Confirmar"
    override val cancelButton = "Cancelar"
    override val deleteButton = "Excluir"
    override val updateButton = "Atualizar"
    override val increaseButton = "Aumentar"
    override val decreaseButton = "Diminuir"

    // Text for messages in general
    override val savedMessage = "Salvo"

    // Menu Options
    override val menuButton = "Menu"
    override val homeMenuButton = "Início"
    override val calendarsMenuButton = "Calendários"
    override val recurrencesMenuButton = "Recorrências"
    override val settingsMenuButton = "Configurações"
    override val backMenuButton = "Voltar"

    // View Options
    override val columnView = "Coluna"
    override val gridView = "Grade"

    // Calendar information
    override val dialogDeleteCalendarTitle = "Excluir calendário"
    override val dialogDeleteCalendarMessage = "Tem certeza de que deseja excluir este calendário?"
    override val calendarNameField = "Nome do calendário"
    override val calendarIsDefaultField = "Definir como padrão"
    override val calendarEmptyNameError = "O nome do calendário não pode estar vazio"
    override val calendarNameMustBeUnique = "O nome do calendário deve ser único"
    override val defaultCalendarCannotBeChanged = "O calendário padrão não pode ser alterado"
    override val defaultCalendarCannotBeDeleted = "O calendário padrão não pode ser excluído"

    // Commitment information
    override val dialogDeleteCommitmentTitle = "Excluir compromisso"
    override val dialogDeleteCommitmentMessage = "Tem certeza de que deseja excluir este compromisso?"
    override val commitmentTitleField = "Título"
    override val commitmentDescriptionField = "Descrição"
    override val commitmentStartField = "Horário de início"
    override val commitmentEndField = "Horário de término"
    override val commitmentPriorityField = "Prioridade"
    override val searchCommitmentField = "Buscar por tarefa"
    override val commitmentNotFoundError = "Compromisso não encontrado"
    override val commitmentStartTimeError = "O horário de início deve ser menor que o horário de término"
    override val commitmentTitleEmptyError = "O título não pode estar vazio"
    override val commitmentConflictError = "Há conflito com outro compromisso"

    // Reminder information
    override val reminderInfo = "Lembrete em %s minutos"
    override val reminderField = "Minutos antes do compromisso"
    override val pastReminderError = "Compromissos no passado não podem ter lembretes"

    // Recurrence information
    override val recurrenceFrequencyField = "Frequência"
    override val recurrenceIntervalField = "Intervalo"
    override val recurrenceDayOfMonthField = "Dia do mês"
    override val recurrenceWeekDaysField = "Dias da semana"
    override val recurrenceIsRecurrenceActiveField = "Esta é uma tarefa recorrente?"
    override val recurrenceValueField = "Valor"

    // Settings information
    override val dialogUpdateSettingTitle = "Confirmar configurações"
    override val dialogUpdateSettingMessage = "Tem certeza de que deseja salvar as configurações?"
    override val settingViewModeField = "Modo de visualização"
    override val settingNotificationField = "Configuração de notificações"

    // Datetime information
    override val monthNames =
        listOf(
            "Janeiro",
            "Fevereiro",
            "Março",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro",
        )
    override val weekDaysAbbrev = listOf('D', 'S', 'T', 'Q', 'Q', 'S', 'S')
    override val dateFormat = "%04d-%02d-%02d"
    override val hourFormat = "%02d:%02d"
}
