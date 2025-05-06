package com.example.contacts.viewmodels

import android.content.Context
import android.provider.ContactsContract
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.models.Contact
import com.example.contacts.models.ContactSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsViewModel: ViewModel() {
    private val _sections = mutableStateListOf<ContactSection>()
    val sections: List<ContactSection> get() = _sections

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

            val grouped = tempContacts
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                .groupBy { it.name.first().uppercaseChar() }

            withContext(Dispatchers.Main) {
                _sections.clear()
                grouped.forEach { (letter, contacts) ->
                    _sections.add(ContactSection(letter, contacts))
                }
            }
        }
    }
}