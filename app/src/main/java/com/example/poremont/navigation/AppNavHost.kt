package com.example.poremont.navigation

import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
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
    }
}
