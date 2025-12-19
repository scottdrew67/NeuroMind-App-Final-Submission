package com.example.neuromind

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ui screen, allows users to rate app 1-5 stars and write a brief review
// reachable from app nav in main activity
fun AppReviewScreen(
    // return to prev screen
    onBack: () -> Unit = {},
    onGoDashboard: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        0.0f to clr_bckgrnd_top,
        0.66f to clr_bckgrnd_top,
        1.0f to clr_bckgrnd_bottom
    )

    val context = LocalContext.current // needed to show toasts

    var rating by remember {mutableIntStateOf(0) } // holds star rating, 0 = not rated yet
    var reviewText by remember {mutableStateOf("")} // holds written feedback

    // standard layout
    Scaffold(
        // transparent so background gradient is shown
        containerColor = Color.Transparent,
        // top bar = title and back button
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rate our app", color = clr_onPrimary) },

                // back arrow
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = clr_onPrimary
                        )
                    }
                },
                // blends app bar into background
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = clr_onPrimary,
                    titleContentColor = clr_onPrimary
                )
            )
        }
    ) { inner ->
        // used  box as root container so background gradient is shown and the full screen is used
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient)
                .padding(inner)
        ) {
            Column( // elements vertically stacked
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header chip panel
                Surface(
                    color = clr_panel_bg,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ratings & Reviews of app performance",
                        color = clr_onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text( // prompt question
                    text = "How would you rate NeuroMind overall?",
                    color = clr_onPrimary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Star rating row
                // outlined when not selected, filled in when selected
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = if (star <= rating)
                                    Icons.Filled.Star
                                else
                                    Icons.Outlined.StarBorder,
                                contentDescription = "$star star",
                                tint = if (star <= rating)
                                    Color(0xFFFFD54F) // highlights selected stars in gold
                                else
                                    clr_onPrimary.copy(alpha = 0.4f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Feedback card, users write their personal review of app
                Surface(
                    color = clr_panel_bg,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tell us about the app’s performance",
                            color = clr_onPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField( // text field for input
                            value = reviewText,
                            onValueChange = { reviewText = it },

                            modifier = Modifier // takes full width, and tall enough for several lines
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            placeholder = { // shown when textbox empty
                                Text(
                                    "e.g. Speed, reliability, crashes, ease of use…",
                                    color = clr_onPrimary.copy(alpha = 0.8f)
                                )
                            },
                            // text wasnt correct colour, used this to ensure it was correct
                            textStyle = LocalTextStyle.current.copy(
                                color = clr_onPrimary
                            ),
                            singleLine = false,
                            maxLines = 6
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // submit review button
                // checks if star rating was given, and feedback was given
                // uses toast messages
                // after submission, clear inputs and navs back to dashboard
                Button(
                    onClick = {
                        if (rating == 0 || reviewText.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please add a star rating and your opinion.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // TODO: Save rating + review to backend / Firebase
                            Toast.makeText(
                                context,
                                "Thank you for your feedback!",
                                Toast.LENGTH_LONG
                            ).show()

                            // clear and go back
                            rating = 0
                            reviewText = ""
                            onGoDashboard()

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = clr_button,
                        contentColor = clr_onPrimary
                    )
                ) {
                    Text("Submit review")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
