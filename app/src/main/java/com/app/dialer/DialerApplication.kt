package com.app.dialer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with [@HiltAndroidApp] to trigger Hilt's code generation
 * and establish the application-level dependency injection component.
 */
@HiltAndroidApp
class DialerApplication : Application()
