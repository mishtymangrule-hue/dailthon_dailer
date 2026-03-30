package com.app.dialer.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module scoped to [SingletonComponent] for use-case bindings.
 *
 * All use cases in this project use `@Inject constructor` with their
 * dependencies directly injected by Hilt — no manual `@Provides` wiring
 * is required. This module is intentionally empty for now and serves as
 * a dedicated namespace for any use-case `@Provides` functions that may
 * be needed when a use case has parameters that cannot be auto-wired
 * (e.g. assisted injection, primitive qualifiers) in future prompts.
 *
 * ### Current injectable use cases (all via @Inject constructor)
 * - [com.app.dialer.domain.usecase.ValidatePhoneNumberUseCase]
 * - [com.app.dialer.domain.usecase.FormatPhoneNumberUseCase]
 * - [com.app.dialer.domain.usecase.InitiateCallUseCase]
 * - [com.app.dialer.domain.usecase.GetSuggestedContactsUseCase]
 * - [com.app.dialer.domain.usecase.GetRecentCallsUseCase]
 * - [com.app.dialer.domain.usecase.GetAvailableSimCardsUseCase]
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
