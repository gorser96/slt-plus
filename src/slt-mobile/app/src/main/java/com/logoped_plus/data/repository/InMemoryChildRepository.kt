package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildRepository

class InMemoryChildRepository : ChildRepository {
    private val children = listOf(
        Child(
            id = "child-1",
            name = "Миша"
        ),
        Child(
            id = "child-2",
            name = "Аня"
        ),
        Child(
            id = "child-3",
            name = "Саша"
        )
    )

    override fun getChildren(): List<Child> {
        return children.toList()
    }

    override fun getChildById(id: String): Child? {
        return children.find { it.id == id }
    }
}