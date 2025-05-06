package com.example.contacts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.contacts.ui.ContactsScreen

@Composable
fun ContactsApp(modifier: Modifier = Modifier) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ContactsScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}