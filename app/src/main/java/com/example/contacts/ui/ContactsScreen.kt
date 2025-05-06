package com.example.contacts.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contacts.models.Contact
import com.example.contacts.viewmodels.ContactsViewModel

@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            viewModel.loadContacts(context)
        }
    }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            viewModel.loadContacts(context)
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    val sections = viewModel.sections

    LazyColumn(
        modifier = modifier
    ) {
        sections.forEach { section ->
            item {
                SectionHeader(section.letter)
            }

            items(section.contacts) { contact ->
                ContactsItem(
                    contact = contact,
                    onClick = { onContactClickCall(contact, context) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(letter: Char) {
    Text(
        text = letter.toString(),
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    )
}

@Composable
fun ContactsItem(contact: Contact, onClick: () -> Unit) {
    Card(
        shape = RectangleShape,
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 48.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
            )
            Column {
                // Contact's name
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                // Contact's phone number
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun onContactClickCall(contact: Contact, context: Context) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = "tel:${contact.phoneNumber}".toUri()
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

@Preview
@Composable
private fun ContactsScreenPreview() {
    ContactsScreen()
}