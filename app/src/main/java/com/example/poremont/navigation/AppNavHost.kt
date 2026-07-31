package com.example.poremont.navigation

import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.poremont.ui.screens.ChecklistScreen
import com.example.poremont.ui.screens.DashboardScreen
import com.example.poremont.ui.screens.DefectCreateScreen
import com.example.poremont.ui.screens.DefectEditScreen
import com.example.poremont.ui.screens.DefectsListScreen
import com.example.poremont.ui.screens.MainScreen
import com.example.poremont.ui.screens.ReferenceListScreen
import com.example.poremont.ui.screens.ReferenceMaterialScreen
import com.example.poremont.ui.screens.ReferenceScreen
import com.example.poremont.ui.screens.RoomSelectionScreen
import com.example.poremont.ui.screens.RoomStagesScreen
import com.example.poremont.ui.screens.StageSelectionScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {

=======
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.poremont.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController) {
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
<<<<<<< HEAD
        composable("main") {
            MainScreen(navController)
        }

        composable("room_selection") {
            RoomSelectionScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable(
            route = "stage_selection/{roomParam}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            StageSelectionScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty()
            )
        }

        composable(
            route = "room_stages/{roomParam}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            RoomStagesScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty()
            )
        }

        composable(
            route = "checklist/{roomParam}/{stageParam}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                },
                navArgument("stageParam") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ChecklistScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty(),
                stageParam = backStackEntry.arguments?.getString("stageParam").orEmpty()
            )
        }

        composable(
            route = "defects_list/{roomParam}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            DefectsListScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty()
            )
        }

        composable(
            route = "defect_create/{roomParam}/{stageParam}/{questionIndex}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                },
                navArgument("stageParam") {
                    type = NavType.StringType
                },
                navArgument("questionIndex") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            DefectCreateScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty(),
                stageParam = backStackEntry.arguments?.getString("stageParam").orEmpty(),
                questionIndex = backStackEntry.arguments?.getInt("questionIndex") ?: 0
            )
        }

        composable(
            route = "defect_edit/{roomParam}/{stageParam}/{questionIndex}",
            arguments = listOf(
                navArgument("roomParam") {
                    type = NavType.StringType
                },
                navArgument("stageParam") {
                    type = NavType.StringType
                },
                navArgument("questionIndex") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            DefectEditScreen(
                navController = navController,
                roomParam = backStackEntry.arguments?.getString("roomParam").orEmpty(),
                stageParam = backStackEntry.arguments?.getString("stageParam").orEmpty(),
                questionIndex = backStackEntry.arguments?.getInt("questionIndex") ?: 0
            )
        }

        composable("reference") {
            ReferenceScreen(navController)
        }

        composable(
            route = "reference_list/{nodeId}",
            arguments = listOf(
                navArgument("nodeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ReferenceListScreen(
                navController = navController,
                nodeId = backStackEntry.arguments?.getString("nodeId").orEmpty()
            )
        }

        composable(
            route = "reference_material/{materialId}",
            arguments = listOf(
                navArgument("materialId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ReferenceMaterialScreen(
                navController = navController,
                materialId = backStackEntry.arguments?.getString("materialId").orEmpty()
            )
        }
=======
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
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
    }
}