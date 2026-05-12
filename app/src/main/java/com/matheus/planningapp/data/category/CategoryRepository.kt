package com.matheus.planningapp.data.category

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategory(categoryId: Long): CategoryEntity?

    fun getAllCategories(): Flow<List<CategoryEntity>>

    fun searchCategories(query: String): Flow<List<CategoryEntity>>

    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    suspend fun updateCategory(categoryEntity: CategoryEntity)

    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    suspend fun getCategoryCount(): Int
}
