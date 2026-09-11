package com.logoped_plus

import android.app.Application

class LogopedPlusApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
