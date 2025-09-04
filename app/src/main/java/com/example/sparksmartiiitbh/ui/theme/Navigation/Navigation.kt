package com.example.sparksmartiiitbh.ui.theme.Navigation
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sparksmartiiitbh.ui.theme.Screens.AdminScreen
import com.example.sparksmartiiitbh.ui.theme.Screens.General
import com.example.sparksmartiiitbh.ui.theme.Screens.Login
import com.example.sparksmartiiitbh.ui.theme.Screens.Signup
import com.example.sparksmartiiitbh.ui.theme.Screens.SplashScreen
import com.example.sparksmartiiitbh.ui.theme.Screens.WorkerScreen
import com.example.sparksmartiiitbh.ui.theme.Screens.WorkerComplaintsScreen
import com.example.sparksmartiiitbh.ui.theme.Screens.ComplaintDetailScreen
import com.example.sparksmartiiitbh.ui.theme.viewModel.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
     authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { !it }) {
            Toast.makeText(
                context,
                "Storage permissions are required for PDF export",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("login") {
            Login(modifier, navController)
        }
        composable("signup") {
            Signup(modifier, navController)
        }
        composable("admin") {
            AdminScreen(
                modifier = modifier,
                navController = navController,
                onRequestPermissions = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                }
            )
        }
        composable("worker") {
            WorkerScreen(modifier, navController)
        }
        composable("workerComplaints") {
            WorkerComplaintsScreen(navController)
        }
        composable("general") {
            General(modifier, navController)
        }
        composable("complaint/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ComplaintDetailScreen(navController, id)
        }
        composable("splash") {
            SplashScreen(navController)
        }
    }
}
