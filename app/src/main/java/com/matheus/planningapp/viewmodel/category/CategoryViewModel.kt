package com.matheus.planningapp.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.category.CategoryEntity
import com.matheus.planningapp.data.category.CategoryRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val strings: StringsRepository,
) : ViewModel() {
    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

    private val _categoryFormUiState: MutableStateFlow<CategoryFormUiState> = MutableStateFlow(CategoryFormUiState())
    val categoryFormUiState = _categoryFormUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<CategoryEntity>> =
        _searchQuery
            .flatMapLatest { query ->
                if (query == null || query.isBlank()) {
                    categoryRepository.getAllCategories()
                } else {
                    categoryRepository.searchCategories(query)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query.ifBlank { null }
    }

    fun onIdChange(id: Long) {
        _categoryFormUiState.update {
            it.copy(id = id)
        }
    }

    fun onNameChange(name: String) {
        _categoryFormUiState.update {
            it.copy(name = name)
        }
    }

    fun onDescriptionChange(description: String) {
        _categoryFormUiState.update {
            it.copy(description = description)
        }
    }

    fun resetForm() {
        _categoryFormUiState.update {
            CategoryFormUiState()
        }
    }

    fun insertCategory() {
        val categoryEntity =
            CategoryEntity(
                name = _categoryFormUiState.value.name,
                description = _categoryFormUiState.value.description.ifBlank { null },
            )
        viewModelScope.launch {
            if (categoryEntity.name.isEmpty()) {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.categoryEmptyNameError),
                )
                return@launch
            }
            try {
                categoryRepository.insertCategory(categoryEntity)
                resetForm()
                _events.emit(DatabaseUiEvent.Saved)
            } catch (e: Exception) {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.categoryInsertError),
                )
            }
        }
    }

    fun updateCategory() {
        viewModelScope.launch {
            val categoryId = _categoryFormUiState.value.id
            if (categoryId == null) {
                _events.emit(DatabaseUiEvent.ShowError(strings.categoryNotFoundError))
                return@launch
            }
            val existingCategory = categoryRepository.getCategory(categoryId)
            if (existingCategory == null) {
                _events.emit(DatabaseUiEvent.ShowError(strings.categoryNotFoundError))
                return@launch
            }
            if (_categoryFormUiState.value.name.isEmpty()) {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.categoryEmptyNameError),
                )
                return@launch
            }
            val updatedCategory =
                existingCategory.copy(
                    name = _categoryFormUiState.value.name,
                    description = _categoryFormUiState.value.description.ifBlank { null },
                )
            try {
                categoryRepository.updateCategory(updatedCategory)
                resetForm()
                _events.emit(DatabaseUiEvent.Saved)
            } catch (e: Exception) {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.categoryUpdateError),
                )
            }
        }
    }

    fun deleteCategory(categoryEntity: CategoryEntity) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryEntity)
                _events.emit(DatabaseUiEvent.Saved)
            } catch (e: Exception) {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.categoryDeleteError),
                )
            }
        }
    }
}
