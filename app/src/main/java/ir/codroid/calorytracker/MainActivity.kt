package ir.codroid.calorytracker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import dagger.hilt.android.AndroidEntryPoint
import ir.codroid.calorytracker.navigation.Route
import ir.codroid.calorytracker.ui.theme.CaloryTrackerTheme
import ir.codroid.core.domain.preferences.Preferences
import ir.codroid.onboarding_presentation.activity.ActivityScreen
import ir.codroid.onboarding_presentation.age.AgeScreen
import ir.codroid.onboarding_presentation.gender.GenderScreen
import ir.codroid.onboarding_presentation.goal.GoalScreen
import ir.codroid.onboarding_presentation.height.HeightScreen
import ir.codroid.onboarding_presentation.nutrient_goal.NutrientGoalScreen
import ir.codroid.onboarding_presentation.weight.WeightScreen
import ir.codroid.onboarding_presentation.welcome.WelcomeScreen
import ir.codroid.tracker_presentation.search.SearchScreen
import ir.codroid.tracker_presentation.tracker_overview.TrackerOverviewScreen
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: Preferences


    @ExperimentalCoilApi
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shouldShowOnboarding = preferences.loadShouldShowOnboarding()
        setContent {
            CaloryTrackerTheme {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState)
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (shouldShowOnboarding) Route.WELCOME else Route.TRACKER_OVERVIEW
                    ) {
                        composable(Route.WELCOME) {
                            WelcomeScreen(onNextClick = {
                                navController.navigate(Route.GENDER)
                            })
                        }
                        composable(Route.AGE) {
                            AgeScreen(
                                snackBarHostState = snackBarHostState,
                                onNextClick = {
                                    navController.navigate(Route.HEIGHT)
                                }
                            )
                        }
                        composable(Route.GENDER) {
                            GenderScreen(onNextClick = {
                                navController.navigate(Route.AGE)
                            })
                        }
                        composable(Route.HEIGHT) {
                            HeightScreen(
                                snackBarHostState = snackBarHostState,
                                onNextClick = {
                                    navController.navigate(Route.WEIGHT)
                                }
                            )
                        }
                        composable(Route.WEIGHT) {
                            WeightScreen(
                                snackBarHostState = snackBarHostState,
                                onNextClick = {
                                    navController.navigate(Route.ACTIVITY)
                                }
                            )
                        }
                        composable(Route.NUTRIENT_GOAL) {
                            NutrientGoalScreen(
                                snackBarHostState = snackBarHostState,
                                onNextClick = {
                                    navController.navigate(Route.TRACKER_OVERVIEW)
                                }
                            )
                        }
                        composable(Route.ACTIVITY) {
                            ActivityScreen(onNextClick = {
                                navController.navigate(Route.GOAL)
                            })
                        }
                        composable(Route.GOAL) {
                            GoalScreen(onNextClick = {
                                navController.navigate(Route.NUTRIENT_GOAL)
                            })
                        }

                        composable(Route.TRACKER_OVERVIEW) {
                            TrackerOverviewScreen(onNavigateToSearch = { mealName, day, month, year ->
                                navController.navigate(
                                    Route.SEARCH +
                                            "/$mealName" +
                                            "/$day" +
                                            "/$month" +
                                            "/$year"
                                )
                            })
                        }
                        composable(
                            route = Route.SEARCH + "/{mealName}/{dayOfMonth}/{month}/{year}",
                            arguments = listOf(
                                navArgument(name = "mealName") {
                                    type = NavType.StringType
                                },
                                navArgument(name = "dayOfMonth") {
                                    type = NavType.IntType
                                },
                                navArgument(name = "month") {
                                    type = NavType.IntType
                                },
                                navArgument(name = "year") {
                                    type = NavType.IntType
                                },
                            )

                        ) {
                            val mealName = it.arguments?.getString("mealName")!!
                            val dayOfMonth = it.arguments?.getInt("dayOfMonth")!!
                            val month = it.arguments?.getInt("month")!!
                            val year = it.arguments?.getInt("year")!!
                            SearchScreen(
                                snackBarHostState = snackBarHostState,
                                mealName = mealName,
                                dayOfMonth = dayOfMonth,
                                month = month,
                                year = year,
                                onNavigateUp = {
                                    navController.navigateUp()
                                })
                        }

                    }
                }


            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CaloryTrackerTheme {
        Greeting("Android")
    }
}