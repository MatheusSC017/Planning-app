package com.matheus.planningapp.viewmodel.home

import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now()
)