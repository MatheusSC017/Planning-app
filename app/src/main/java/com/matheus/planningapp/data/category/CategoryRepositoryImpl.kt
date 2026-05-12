package com.matheus.planningapp.data.category

import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override suspend fun getCategory(categoryId: Long): CategoryEntity? = categoryDao.getCategory(categoryId)

    override fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    override fun searchCategories(query: String): Flow<List<CategoryEntity>> = categoryDao.searchCategories(query)

    override suspend fun insertCategory(categoryEntity: CategoryEntity): Long = categoryDao.insert(categoryEntity)

    override suspend fun updateCategory(categoryEntity: CategoryEntity) {
        categoryDao.update(categoryEntity)
    }

    override suspend fun deleteCategory(categoryEntity: CategoryEntity) {
        categoryDao.delete(categoryEntity)
    }

    override suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()
}
