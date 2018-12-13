package com.sergiocruz.nanogram.viewmodel

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContentResolverCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel : ViewModel() {
    private lateinit var contacts: MutableLiveData<Cursor>

    fun getContacts(context: Context): LiveData<Cursor> {
        if (!::contacts.isInitialized) {
            contacts = MutableLiveData()
            loadContacts(context)
        }
        return contacts
    }

    private fun loadContacts(context: Context): Cursor? {
        // Asynchronous operation to fetch contacts.
        return ContentResolverCompat.query(
            context.contentResolver,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(
                ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
            ), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
                ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE
            ),

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC",
            null
        )
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        )
        const val ADDRESS = 0
        const val IS_PRIMARY = 1
    }
}