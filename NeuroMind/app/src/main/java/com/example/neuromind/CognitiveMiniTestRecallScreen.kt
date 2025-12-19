package com.example.neuromind

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



// users recall words shown to them
// user types as many animals as they can in 30 seconds
// calculates memory score from the 2 tasks
// passes that score to mainactivity through onMemoryScored
// moves on to runRiskScreen when onFinished is called
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CognitiveMiniTestRecallScreen(
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {}, // called when user completes screen
    onMemoryScored: (Double) -> Unit = {} // returns memory Score to mainactivity
) {
    val gradient = Brush.verticalGradient(
        0.0f to clr_bckgrnd_top,
        0.66f to clr_bckgrnd_top,
        1.0f to clr_bckgrnd_bottom
    )


    // words shown to user in previous page
    val wordOptions = listOf(
        "Mango",
        "Banana",
        "Pear",
        "Apple",
        "Orange",
        "Kiwi",
        "Strawberry"
    )

    // stores which words user has ticked, and what animals they have inputted
    var selectedWords by remember { mutableStateOf(setOf<String>()) }
    var animalsText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("3. Cognitive Mini-Test", color = clr_onPrimary) },
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
        Box( // full screen with gradient background
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient)
        ) {
            Column( // ui is stacked from top to bottom
                modifier = Modifier
                    .padding(inner)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text( // instruction for word recall section
                    text = "Please choose the 5 words you remember",
                    color = clr_onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(16.dp))

                // White card with word checkboxes
                Surface(
                    color = clr_panel_bg,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        wordOptions.forEach { word -> // each word is a row with checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedWords.contains(word),
                                    // toggle for adding/removing word from selected
                                    onCheckedChange = {
                                        selectedWords =
                                            if (selectedWords.contains(word)) {
                                                selectedWords - word
                                            } else {
                                                selectedWords + word
                                            }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = clr_button
                                    )
                                )
                                Text(text = word, color = clr_onPrimary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // instruction bar for animal naming task
                Surface(
                    color = clr_recall_hint_bg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Type as many animals as you can in 30 seconds",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Text area for animals, multilined
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                ) {
                    OutlinedTextField(
                        value = animalsText,
                        onValueChange = { animalsText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text("e.g. Fox") },
                        singleLine = false,
                        minLines = 4,
                        maxLines = 6
                    )
                }

                // Push bottom content down a bit
                Spacer(Modifier.weight(1f))

                // progress indicator, meant to implement it on medical questionnaire and speech
                // test, but never did
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    // currently, always true, so all bars are coloured
                                    if (index <= 2) clr_progress_ok else Color.White,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }


                // finishes memory score
                // returns through onMemoryScore and navs forward through onFinish
                Button(
                    onClick = {
                        val wordsRemembered = selectedWords.size // 0-5 selected words

                        val animalCount = animalsText  // counts animals typed
                            .split(",", " ", "\n")// counts by using spaces+commas
                            .filter{it.isNotBlank()}
                            .size

                        // 0-5, capped at 15, divide by 3 (15 = 5)
                        val animalsScore = (animalCount.coerceIn(0, 15) / 3.0)

                        // combines  words remembered, animals score, and gets average
                        val memoryScore = (wordsRemembered + animalsScore) / 2

                        onMemoryScored(memoryScore) // sends score back to mainactivity
                        onFinish()// continue to next screen
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
                    Text("Finish")
                }
            }
        }
    }
}
