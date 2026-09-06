package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.Child
import kotlinx.coroutines.flow.StateFlow

interface ChildRepository {

    val children: StateFlow<List<Child>>

    fun getChildById(id: String): Child?

    fun addChild(child: Child)

    fun updateChild(child: Child)
}