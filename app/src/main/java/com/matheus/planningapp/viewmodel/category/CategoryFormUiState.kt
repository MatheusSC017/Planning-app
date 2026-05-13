package com.matheus.planningapp.viewmodel.category

data class CategoryFormUiState(
    val id: Long? = null,
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = true,
)
