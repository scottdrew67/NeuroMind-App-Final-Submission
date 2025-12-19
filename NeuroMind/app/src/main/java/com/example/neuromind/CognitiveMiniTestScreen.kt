package com.example.neuromind

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.blur



// memory preview step
// user sees 5 words once started, has 7 seconds to remember them
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CognitiveMiniTestScreen(
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        0.0f to clr_bckgrnd_top,
        0.66f to clr_bckgrnd_top,
        1.0f to clr_bckgrnd_bottom
    )

    // --- STATE ---
    var isBlurred by remember { mutableStateOf(true) }        // start blurred
    var timerRunning by remember { mutableStateOf(false) }    // control when timer begins
    var timeLeft by remember { mutableIntStateOf(7) }         // 7 seconds

    // --- TIMER EFFECT ---
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timeLeft > 0) {
                delay(1000) // every second
                timeLeft-- // reduce countdown
            }
            if (timeLeft == 0) {
                onNext()   // navigates to recall screen when time hits 0
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("3. Cognitive Mini-Test", color = clr_onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) { // back arrow
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = clr_onPrimary
                        )
                    }
                },
                // background gradient wasnt showing at the top originally, had to add this
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
        ) {
            Column(
                modifier = Modifier
                    .padding(inner)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //  header chip
                Surface(
                    color = clr_button,
                    shape = RoundedCornerShape(10.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Text(
                        text = "Cognitive Memory Exercise",
                        color = clr_onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text( // instructions for user
                    "You will see 5 words.\nTry to remember them for the next screen.",
                    color = clr_onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                // Card holding the coloured words, starts blurred
                Surface(
                    color = clr_panel_bg,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    // tap to reveal & start timer on click
                    onClick = {
                        if (isBlurred) {
                            isBlurred = false
                            if (!timerRunning) { // makes sure timer only starts once
                                timerRunning = true
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            // blur applied, while isBlurred = true
                            .blur(if (isBlurred) 16.dp else 0.dp)
                    ){
                        // 5 words underneath, placed in different areas of box
                        Text(
                            text = "Apple",
                            color = clr_word_apple,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                        )
                        Text(
                            text = "Orange",
                            color = clr_word_orange,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                        )
                        Text(
                            text = "Banana",
                            color = clr_word_banana,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )
                        Text(
                            text = "Mango",
                            color = clr_word_mango,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                        )
                        Text(
                            text = "Kiwi",
                            color = clr_word_kiwi,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                        )

                        // Blurred overlay
                        if (isBlurred) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x55000000)),   // semi-transparent
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tap to reveal the words",
                                    color = Color.White)
                            }
                        }

                    }
                }

                Spacer(Modifier.height(24.dp))

                // Info bar - dynamic text depending on state
                // when blurred - tells user to tap and explains timer
                // when not blurred - shows countdown
                val infoText = when {
                    isBlurred -> "Tap the box to reveal the words.\nThe 7-second timer will start then."
                    else -> "The words will disappear in\n$timeLeft seconds"
                }

                Surface(
                    color = clr_dark_info_bar,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = infoText,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
