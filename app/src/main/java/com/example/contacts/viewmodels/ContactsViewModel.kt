package com.example.contacts.viewmodels

import android.content.Context
import android.provider.ContactsContract
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsViewModel: ViewModel() {
    private val _contacts = mutableStateListOf<Contact>()
    val contacts: List<Contact> get() = _contacts

    fun loadContacts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempContacts = mutableListOf<Contact>()

            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                null
            )

            cursor?.use {
                while(it.moveToNext()) {
                    val name = it.getString(
                        it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    )
                    val phoneNumber = it.getString(
                        it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )

                    tempContacts.add(Contact(name.ifEmpty { "No name" }, phoneNumber))
                }
            }

            withContext(Dispatchers.Main) {
                _contacts.clear()
                _contacts.addAll(
                    tempContacts.sortedWith(
                        compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
                    )
                )
            }
        }
    }
}