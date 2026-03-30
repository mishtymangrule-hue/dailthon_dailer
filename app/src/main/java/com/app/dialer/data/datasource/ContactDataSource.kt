package com.app.dialer.data.datasource

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.app.dialer.domain.model.SuggestedContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Low-level data source that queries Android's [ContactsContract] ContentProvider.
 *
 * All public methods are synchronous and expected to be called from a background
 * dispatcher (e.g. [kotlinx.coroutines.Dispatchers.IO]). Each method opens and
 * closes a [Cursor] within a `use` block to prevent leaks.
 *
 * ### Permissions
 * Queries that read contact data require [android.Manifest.permission.READ_CONTACTS].
 * The data source assumes the permission is granted by the time any method is called —
 * permission checking is enforced at the entry-point layer ([DialerPermissionScreen]).
 */
@Singleton
class ContactDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "ContactDataSource"
        private const val DEFAULT_LIMIT = 20

        private val PHONE_PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Phone.STARRED,
            @Suppress("DEPRECATION")
            ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED
        )

        private val PHONE_LOOKUP_PROJECTION = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
            ContactsContract.PhoneLookup.STARRED,
            ContactsContract.PhoneLookup.NUMBER
        )
    }

    // ─── Public API ──────────────────────────────────────────────────────────

    /**
     * Returns contacts for the dial-pad suggestion row.
     *
     * - Empty [query]: returns starred contacts first, then the most-frequently-
     *   called contacts, up to [limit] unique entries.
     * - Non-empty [query]: filters by display name or number substring, up to [limit].
     */
    fun getSuggestedContacts(query: String, limit: Int): List<SuggestedContact> {
        return if (query.isBlank()) {
            queryFeaturedContacts(limit)
        } else {
            queryByNameOrNumber(query, limit)
        }
    }

    /**
     * Returns an unbounded list of contacts whose display name or phone number
     * contains [query] (case-insensitive substring match).
     */
    fun searchContacts(query: String): List<SuggestedContact> {
        return queryByNameOrNumber(query, limit = null)
    }

    /**
     * Performs a reverse phone-number lookup using
     * [ContactsContract.PhoneLookup.CONTENT_FILTER_URI].
     *
     * @return The first matching [SuggestedContact], or `null` if not found.
     * @throws Exception on ContentProvider error (caller converts to [Result.failure]).
     */
    fun lookupByNumber(phoneNumber: String): SuggestedContact? {
        if (phoneNumber.isBlank()) return null
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        return context.contentResolver.query(
            uri, PHONE_LOOKUP_PROJECTION, null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.toSuggestedContactFromLookup() else null
        }
    }

    // ─── Private queries ─────────────────────────────────────────────────────

    /** Starred + most-called contacts for idle dial-pad state. */
    private fun queryFeaturedContacts(limit: Int): List<SuggestedContact> {
        val sortOrder = buildString {
            append("${ContactsContract.CommonDataKinds.Phone.STARRED} DESC, ")
            @Suppress("DEPRECATION")
            append("${ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED} DESC")
        }
        return runPhoneQuery(
            selection = null,
            selectionArgs = null,
            sortOrder = sortOrder,
            limit = limit
        )
    }

    /** Contacts whose name or number contains [query]. */
    private fun queryByNameOrNumber(query: String, limit: Int?): List<SuggestedContact> {
        val selection =
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} LIKE ? OR " +
            "${ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER} LIKE ?"
        val pattern = "%$query%"
        return runPhoneQuery(
            selection = selection,
            selectionArgs = arrayOf(pattern, pattern),
            sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC",
            limit = limit
        )
    }

    /**
     * Generic helper that queries [ContactsContract.CommonDataKinds.Phone.CONTENT_URI],
     * applies an optional SQL LIMIT via the URI, and maps each row to [SuggestedContact].
     *
     * Duplicate contact IDs are deduplicated (one row per contact, keeping the
     * highest-ranked phone number row).
     */
    private fun runPhoneQuery(
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        limit: Int?
    ): List<SuggestedContact> {
        val baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val uri = if (limit != null) {
            baseUri.buildUpon()
                .appendQueryParameter(ContactsContract.LIMIT_PARAM_KEY, limit.toString())
                .build()
        } else {
            baseUri
        }

        return try {
            context.contentResolver.query(
                uri, PHONE_PROJECTION, selection, selectionArgs, sortOrder
            )?.use { cursor ->
                buildList {
                    val seenIds = mutableSetOf<Long>()
                    while (cursor.moveToNext()) {
                        val contact = cursor.toSuggestedContactFromPhone()
                        if (seenIds.add(contact.id)) add(contact)
                    }
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "runPhoneQuery failed", e)
            emptyList()
        }
    }

    // ─── Cursor mappers ───────────────────────────────────────────────────────

    private fun Cursor.toSuggestedContactFromPhone(): SuggestedContact {
        @Suppress("DEPRECATION")
        val timesContactedIdx =
            getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED)

        return SuggestedContact(
            id = getLong(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)),
            displayName = getString(
                getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            ) ?: "",
            phoneNumber = getString(
                getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            ) ?: "",
            photoUri = getString(
                getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
            )?.let { Uri.parse(it) },
            isStarred = getInt(
                getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.STARRED)
            ) == 1,
            callCount = if (timesContactedIdx >= 0) getInt(timesContactedIdx) else 0
        )
    }

    private fun Cursor.toSuggestedContactFromLookup(): SuggestedContact {
        return SuggestedContact(
            id = getLong(getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID)),
            displayName = getString(
                getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
            ) ?: "",
            phoneNumber = getString(
                getColumnIndexOrThrow(ContactsContract.PhoneLookup.NUMBER)
            ) ?: "",
            photoUri = getString(
                getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)
            )?.let { Uri.parse(it) },
            isStarred = getInt(
                getColumnIndexOrThrow(ContactsContract.PhoneLookup.STARRED)
            ) == 1,
            callCount = 0
        )
    }
}
