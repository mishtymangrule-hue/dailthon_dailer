package com.app.dialer.core.di

import com.app.dialer.data.repository.DialerContactRepositoryImpl
import com.app.dialer.domain.repository.DialerContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds [DialerContactRepository] to its real ContentResolver-
 * backed implementation [DialerContactRepositoryImpl].
 *
 * Separated from [RepositoryModule] so that the contact-data layer can be
 * swapped or faked in isolation during tests.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ContactModule {

    @Binds
    @Singleton
    abstract fun bindDialerContactRepository(
        impl: DialerContactRepositoryImpl
    ): DialerContactRepository
}
