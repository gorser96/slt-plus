package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.Child
import kotlinx.coroutines.flow.StateFlow

interface ChildRepository {

    val state: StateFlow<ChildLoadState>
    fun retryLoading()
    suspend fun addChild(child: Child): ChildWriteResult
    suspend fun updateChild(child: Child): ChildWriteResult
}
