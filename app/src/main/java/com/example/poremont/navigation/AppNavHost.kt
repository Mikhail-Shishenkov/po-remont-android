package com.example.poremont.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.poremont.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainScreen(navController) }
        composable("room_selection") { RoomSelectionScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("stage_selection/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: 0
            StageSelectionScreen(navController, roomId)
        }
        composable("stages_list/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: 0
            StagesListScreen(navController, roomId)
        }
        composable("checklist/{stageId}") { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId")?.toIntOrNull() ?: 0
            ChecklistScreen(navController, stageId)
        }
        composable("defect_create/{stageId}") { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId")?.toIntOrNull() ?: 0
            DefectCreateScreen(navController, stageId)
        }
        composable("defect_edit/{defectId}") { backStackEntry ->
            val defectId = backStackEntry.arguments?.getString("defectId")?.toIntOrNull() ?: 0
            DefectEditScreen(navController, defectId)
        }
        composable("defects_list/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: 0
            DefectsListScreen(navController, roomId)
        }
        composable("reference") { ReferenceScreen(navController) }
    }
}