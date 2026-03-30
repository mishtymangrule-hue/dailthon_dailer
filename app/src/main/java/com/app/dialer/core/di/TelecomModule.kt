package com.app.dialer.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module placeholder for the Telecom subsystem.
 *
 * All Telecom singletons in this codebase ([CallEventBus], [CallNotificationManager],
 * [CallManagerService] helpers, etc.) are annotated with `@Singleton` and use
 * `@Inject constructor`, so Hilt wires them automatically without explicit
 * `@Provides` or `@Binds` declarations here.
 *
 * This module exists to provide a clear DI home for any future Telecom-specific
 * bindings that cannot be expressed via constructor injection (e.g. binding a
 * platform type or an abstract interface to a concrete implementation).
 */
@Module
@InstallIn(SingletonComponent::class)
object TelecomModule
