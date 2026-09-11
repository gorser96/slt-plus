package com.logoped_plus.domain.repository

sealed interface ChildWriteResult {
    data object Success : ChildWriteResult
    data class Failure(val reason: Reason) : ChildWriteResult
    enum class Reason { InvalidName, NotFound, Conflict, StorageUnavailable, NotReady }
}
