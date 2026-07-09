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
    override val noValuesFound = "Nenhum valor encontrado"

    // Menu Options
    override val menuButton = "Menu"
    override val homeMenuButton = "Início"
    override val calendarsMenuButton = "Calendários"
    override val recurrencesMenuButton = "Recorrências"
    override val settingsMenuButton = "Configurações"
    override val backMenuButton = "Voltar"
    override val focusModeMenuButton = "Modo Foco"

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
    override val reminderListTitle = "Lembretes ativos"
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

    // Category information
    override val categoryManagementTitle = "Categorias"
    override val categoryNameField = "Nome"
    override val categoryDescriptionField = "Descrição"
    override val searchCategoryField = "Pesquisar"
    override val noCategorySelected = "Nenhuma categoria selecionada"
    override val createCategoryTitle = "Criar Nova Categoria"
    override val updateCategoryTitle = "Atualizar Categoria"
    override val categoryEmptyNameError = "O nome da categoria não pode estar vazio"
    override val categoryNotFoundError = "Categoria não encontrada"
    override val categoryInsertError = "Erro ao criar categoria"
    override val categoryUpdateError = "Erro ao atualizar categoria"
    override val categoryDeleteError = "Erro ao excluir categoria"
    override val noCategoriesFound = "Nenhuma categoria encontrada"
    override val categoryDeletedMessage = "Categoria excluída com sucesso"
    override val editButton = "Editar"
    override val searchError = "Erro ao pesquisar categorias"

    // Focus mode information
    override val stopFocusModeButton = "Parar Modo Foco"
    override val dialogStopFocusTitle = "Parar modo foco?"
    override val dialogStopFocusMessage = "Tem certeza de que deseja parar o modo foco?"

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
    override val dateFormat = "dd/MM/yyyy"
    override val hourFormat = "%02d:%02d"
}
