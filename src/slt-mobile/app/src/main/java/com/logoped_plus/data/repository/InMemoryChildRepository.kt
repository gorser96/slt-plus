package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryChildRepository : ChildRepository {

    private val _children = MutableStateFlow(
        listOf(
            Child(
                id = "child-1",
                name = "Иван Иванов"
            ),
            Child(
                id = "child-2",
                name = "Мария Петрова"
            ),
            Child(
                id = "child-3",
                name = "Алексей Сидоров"
            )
        )
    )

    override val children: StateFlow<List<Child>> =
        _children.asStateFlow()

    override fun getChildById(id: String): Child? {
        return _children.value.find { it.id == id }
    }

    override fun addChild(child: Child) {
        _children.value = _children.value + child
    }

    override fun updateChild(child: Child) {
        _children.value = _children.value.map { existingChild ->
            if (existingChild.id == child.id) {
                child
            } else {
                existingChild
            }
        }
    }
}