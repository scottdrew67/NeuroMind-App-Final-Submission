package com.example.neuromind

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// function for email button, launches email app with pre filled email
private fun openEmail(
    context: android.content.Context,
    to: String,
    subject: String,
    body: String
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$to") // ensures only email apps handle it
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    // only starts if there IS an email app on device (none on my emulator)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    onBack: () -> Unit = {},
    onOpenRatings: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        0.0f to clr_bckgrnd_top,
        0.66f to clr_bckgrnd_top,
        1.0f to clr_bckgrnd_bottom
    )

    // local state for users message
    var message by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Need Support?", color = clr_onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = clr_onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = clr_onPrimary,
                    titleContentColor = clr_onPrimary
                )
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient)
                .padding(inner)
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // -------- MESSAGE INPUT PANEL --------
                Surface(
                    color = clr_panel_bg,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Message", color = clr_onPrimary, fontSize = 16.sp)
                        Spacer(Modifier.height(20.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = clr_onPrimary,
                                unfocusedTextColor = clr_onPrimary
                            )
                        )

                    }
                }

                Spacer(Modifier.height(12.dp))

                val context = LocalContext.current // used for toasts and start of email


                Button(
                    onClick = {
                        if (message.isBlank()) { // button checks if message isnt empty
                            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
                        } else {
                            // opens email app with message filled
                            openEmail(
                                context = context,
                                to = "neuromindsystem@gmail.com",
                                subject = "NeuroMind Support Request",
                                body = "Support message:\n\n$message\n\n— Sent from NeuroMind"
                            )
                            message = "" // clears after launching email app
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = clr_button,
                        contentColor = clr_onPrimary
                    )
                ) {
                    Text("Send")
                }


                Spacer(Modifier.weight(1f))

                Button( // goes to ratings page
                    onClick = onOpenRatings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = clr_button,
                        contentColor = clr_onPrimary
                    )
                ) {
                    Text("Rate our app")
                }
            }
        }
    }
}
