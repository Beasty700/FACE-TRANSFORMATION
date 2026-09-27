package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM transformation_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<TransformationProject>>

    @Query("SELECT * FROM transformation_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): TransformationProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: TransformationProject): Long

    @Update
    suspend fun updateProject(project: TransformationProject)

    @Query("DELETE FROM transformation_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("DELETE FROM transformation_projects")
    suspend fun deleteAllProjects()

    // Identity References
    @Query("SELECT * FROM identity_references ORDER BY timestamp ASC")
    fun getAllReferences(): Flow<List<IdentityReferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(ref: IdentityReferenceEntity): Long

    @Query("DELETE FROM identity_references WHERE id = :id")
    suspend fun deleteReferenceById(id: Long)

    @Query("DELETE FROM identity_references")
    suspend fun deleteAllReferences()
}
