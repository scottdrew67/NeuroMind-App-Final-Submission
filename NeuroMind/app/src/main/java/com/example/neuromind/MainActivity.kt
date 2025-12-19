package com.example.neuromind

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this) // init firebase

        setContent { AppNav() } // host compose navigation
    }
}

    @Composable
    fun AppNav() { // app wide nav graph, holds all route mappings
        val navController = rememberNavController() // controls which screen is shown

        // Shared score state for dashboard + result screen
        var lastRiskScore by rememberSaveable { mutableStateOf<Int?>(null)}

        // values saved throughout assessment
        var assessmentAge by rememberSaveable { mutableStateOf<Double?>(null) }
        var assessmentSleep by rememberSaveable { mutableStateOf<Double?>(null) }
        var assessmentMemory by rememberSaveable { mutableStateOf<Double?>(null) }
        var assessmentSpeechText by rememberSaveable { mutableStateOf<String?>(null) }

        NavHost(
            navController = navController,
            startDestination = "login" // first screen is login
        ) {
            // on login success, go to dashboard screen
            composable("login") {
                LoginScreen(
                    onLoggedIn = { name ->
                        val safe = android.net.Uri.encode(name) // encode name for spaces/special chars
                        navController.navigate("dashboard/$safe") {
                            popUpTo("login") { inclusive = true } // remove login from backstack
                        }
                    },
                    onForgotPassword = {
                        navController.navigate("forgot_password")
                    }
                )
            }

            // display latest risk score if any, and nav shortcuts to features
            composable(
                route = "dashboard/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            )
            { backStackEntry ->
                // pulls name from nav arguments, User by default
                val name = backStackEntry.arguments?.getString("name") ?: "User"
                DashboardScreen(
                    name = name,
                    lastRiskScore = lastRiskScore, // latest score shown on dash
                    onOpenCognitive = {navController.navigate("cognitive_test")},

                    // due to redundant test page, onOpenTest now points to medical assessment screen
                    // kept in case need of future testing
                    onOpenTest = { navController.navigate("medical_assessment") },
                    onOpenSpeech = { navController.navigate("speech_test") },
                    onOpenMedical = { navController.navigate("medical_assessment") },
                    onOpenRisk = {navController.navigate("risk_result")},
                    onOpenContact = {navController.navigate("contact_support")},

                    // sign out clears firebase session, return to login, and clear back stack
                    onSignOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenNews = { navController.navigate("patient_news") },
                    onOpenPayments = { navController.navigate("payments")},
                    onOpenRatings = {navController.navigate("app_review")}
                )
            }


            composable("run_risk") { // uses 4 collected values, calls the backend via helper
                RunRiskScreen(
                    age = assessmentAge,
                    sleep = assessmentSleep,
                    memory = assessmentMemory,
                    speechText = assessmentSpeechText,
                    onBack = { navController.popBackStack() },
                    // when final score is received, store it in results
                    onResult = { score ->
                        lastRiskScore = score
                        navController.navigate("risk_result")
                    }
                )
            }

            // user can rate/submit feedback
            composable("app_review") {
                AppReviewScreen(
                    onBack = { navController.popBackStack() },
                    onGoDashboard = {navController.popBackStack(route = "dashboard/{name}",inclusive = false)}
                )
            }

            // forgot password screen
            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // payment screen that uses sandbox links (no real payments)
            composable("payments") {
                PaymentsScreen(
                    onBack = { navController.popBackStack() }
                )
            }


            // patient news screen uses backend.py to load news
            composable("patient_news") {
                PatientNewsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // back button returns to previous screen, can also access the rate app page
            composable("contact_support") {
                ContactSupportScreen(
                    onBack = { navController.popBackStack() },
                    onOpenRatings = { navController.navigate("app_review") }
                )
            }

            // display lastRiskScore from the shared state
            composable("risk_result"){
                DementiaResultScreen(
                    score = lastRiskScore,
                    onBack = {navController.popBackStack()},
                    onContactSupport = {navController.navigate("contact_support")}
                )
            }


            // cognitive test flow
            // cognitive_test -> cognitive_recall -> run_risk
            composable("cognitive_test"){
                CognitiveMiniTestScreen(
                    onBack = {navController.popBackStack()},
                    onNext = {navController.navigate("cognitive_recall")}
                )
            }
            composable("cognitive_recall"){
                CognitiveMiniTestRecallScreen(
                    onBack = {navController.popBackStack()},
                    // after recal finished, proceed to risk eval screen
                    onFinish = {navController.navigate("run_risk")},
                    // save memory score result so runRiskScreen can use it too
                    onMemoryScored = {score ->
                        assessmentMemory = score
                    }
                )
            }

            // collects age and sleep, then sends user to speech_test
            composable("medical_assessment") {
                MedicalAssessmentScreen(
                    onBack = { navController.popBackStack() },
                    onNext = {age, sleep ->
                        // store values in shared state
                        assessmentAge = age
                        assessmentSleep = sleep
                        // and continue
                        navController.navigate("speech_test")
                    }
                    )
            }

            // takes transcript/ summary of text and stores it
            // then proceed to cognitive test
            composable("speech_test") {
                SpeechTestScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("cognitive_test") },

                    // save speech so runRiskScreen can use it
                    onSpeechReady = {text ->
                        assessmentSpeechText = text
                    }
                )
            }
        }
    }


