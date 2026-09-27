package com.example.data

import com.example.data.local.IdentityReferenceEntity
import com.example.data.local.ProjectDao
import com.example.data.local.TransformationProject
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: ProjectDao) {

    val allProjects: Flow<List<TransformationProject>> = dao.getAllProjects()
    val allReferences: Flow<List<IdentityReferenceEntity>> = dao.getAllReferences()

    suspend fun getProjectById(id: Long): TransformationProject? = dao.getProjectById(id)

    suspend fun saveProject(project: TransformationProject): Long = dao.insertProject(project)

    suspend fun updateProject(project: TransformationProject) = dao.updateProject(project)

    suspend fun deleteProject(id: Long) = dao.deleteProjectById(id)

    suspend fun deleteAllProjects() = dao.deleteAllProjects()

    suspend fun addReference(ref: IdentityReferenceEntity): Long = dao.insertReference(ref)

    suspend fun deleteReference(id: Long) = dao.deleteReferenceById(id)

    suspend fun deleteAllReferences() = dao.deleteAllReferences()
}
