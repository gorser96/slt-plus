package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.Child

interface ChildRepository {

    fun getChildren(): List<Child>

    fun getChildById(id: String): Child?
}