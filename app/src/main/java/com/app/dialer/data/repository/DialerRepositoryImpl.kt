package com.app.dialer.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.app.dialer.core.telecom.TelecomManagerHelper
import com.app.dialer.core.utils.SimCardHelper
import com.app.dialer.data.local.RecentCallDao
import com.app.dialer.data.model.RecentCallEntity
import com.app.dialer.domain.model.RecentCall
import com.app.dialer.domain.model.SimCard
import com.app.dialer.domain.repository.DialerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of [DialerRepository].
 *
 * | Operation          | Backing store                                    |
 * |--------------------|--------------------------------------------------|
 * | initiateCall       | [TelecomManagerHelper.initiateOutgoingCall]      |
 * | getRecentCalls     | [RecentCallDao.getRecentCalls] → domain mapper   |
 * | markCallAsRead     | [RecentCallDao.markAsRead]                       |
 * | deleteCallLog      | [RecentCallDao.deleteById]                       |
 * | getAvailableSimCards | [SimCardHelper.getActiveSimCards]              |
 * | getDefaultSimCard  | DataStore preference (subscriptionId key)        |
 *
 * All suspend operations run on [Dispatchers.IO]. Flow operations use
 * `.flowOn(Dispatchers.IO)` to keep Room and DataStore work off the Main thread.
 */
@Singleton
class DialerRepositoryImpl @Inject constructor(
    private val telecomManagerHelper: TelecomManagerHelper,
    private val recentCallDao: RecentCallDao,
    private val simCardHelper: SimCardHelper,
    private val dataStore: DataStore<Preferences>
) : DialerRepository {

    companion object {
        /** DataStore key storing the user-preferred default SIM subscription ID. */
        private val DEFAULT_SIM_KEY = intPreferencesKey("default_sim_subscription_id")
    }

    // ─── DialerRepository implementation ─────────────────────────────────────

    override suspend fun initiateCall(
        phoneNumber: String,
        subscriptionId: Int?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        telecomManagerHelper.initiateOutgoingCall(phoneNumber, subscriptionId)
    }

    override fun getRecentCalls(limit: Int): Flow<List<RecentCall>> =
        recentCallDao.getRecentCalls(limit)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)

    override suspend fun markCallAsRead(callId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { recentCallDao.markAsRead(callId) }
        }

    override suspend fun deleteCallLog(callId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { recentCallDao.deleteById(callId) }
        }

    override suspend fun getAvailableSimCards(): Result<List<SimCard>> =
        withContext(Dispatchers.IO) {
            runCatching { simCardHelper.getActiveSimCards() }
        }

    /**
     * Emits the user's preferred default SIM card by reading the stored subscription
     * ID from DataStore and looking it up in the active subscriptions.
     *
     * Resolution order:
     * 1. If a preferred subscription ID is stored in DataStore, find that SIM.
     * 2. If not (first launch / cleared), fall back to the system-default
     *    voice SIM ([SimCard.isDefault]).
     * 3. If neither is available, emits null (no SIM / single SIM with no preference).
     */
    override fun getDefaultSimCard(): Flow<SimCard?> =
        dataStore.data
            .map { prefs ->
                val preferredSubId = prefs[DEFAULT_SIM_KEY]
                val activeSims = simCardHelper.getActiveSimCards()
                when {
                    preferredSubId != null ->
                        activeSims.firstOrNull { it.subscriptionId == preferredSubId }
                    else ->
                        activeSims.firstOrNull { it.isDefault }
                }
            }
            .flowOn(Dispatchers.IO)

    // ─── Entity → Domain mapper ───────────────────────────────────────────────

    private fun RecentCallEntity.toDomain() = RecentCall(
        id = id,
        contactName = contactName,
        phoneNumber = phoneNumber,
        callType = callType,
        durationSeconds = durationSeconds,
        timestamp = timestamp,
        photoUri = photoUri?.let { Uri.parse(it) },
        isRead = isRead,
        simSlotIndex = simSlotIndex
    )
}
