@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.crskdev.mealcalculator

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crskdev.mealcalculator.data.FoodRepositoryImpl
import com.crskdev.mealcalculator.data.MealRepositoryImpl
import com.crskdev.mealcalculator.data.RecipeRepositoryImpl
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.gateway.*
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.platform.PlatformGatewayDispatchers
import com.crskdev.mealcalculator.platform.PlatformRecipeFoodEntriesManager
import com.crskdev.mealcalculator.platform.router.BackRouterImpl
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModel
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel
import com.crskdev.mealcalculator.presentation.home.HomeViewModel
import com.crskdev.mealcalculator.presentation.meal.MealJournalDetailViewModel
import com.crskdev.mealcalculator.presentation.meal.MealJournalViewModel
import com.crskdev.mealcalculator.presentation.meal.MealViewModel
import com.crskdev.mealcalculator.presentation.recipe.RecipeFoodsViewModel
import com.crskdev.mealcalculator.presentation.recipe.RecipeUpsertViewModel
import com.crskdev.mealcalculator.presentation.recipe.RecipesDisplayViewModel
import com.crskdev.mealcalculator.ui.common.di.BaseDependencyGraph
import com.crskdev.mealcalculator.ui.food.*
import com.crskdev.mealcalculator.ui.home.HomeFragment
import com.crskdev.mealcalculator.ui.home.HomeViewModelRouterImpl
import com.crskdev.mealcalculator.ui.meal.*
import com.crskdev.mealcalculator.ui.recipe.*
import com.crskdev.mealcalculator.utils.viewModelFromProvider
import com.google.firebase.FirebaseApp
import io.fabric.sdk.android.Fabric

/**
 * Created by Cristian Pela on 28.01.2019.
 */
class MealCalculatorApplication : Application() {

    lateinit var dependencyGraph: DependencyGraph

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            // .debuggable(true)
            .build()
        Fabric.with(fabric)
        dependencyGraph = DependencyGraph(this)
    }
}

fun Context.dependencyGraph() =
    this.applicationContext.cast<MealCalculatorApplication>().dependencyGraph

class DependencyGraph(context: Context) : BaseDependencyGraph(context) {

    val db: MealCalculatorDatabase by lazy {
        MealCalculatorDatabase.persistent(context)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepositoryImpl(db)
    }

    val mealRepository: MealRepository by lazy {
        MealRepositoryImpl(db)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl(db)
    }

    val dispatchers: GatewayDispatchers by lazy {
        PlatformGatewayDispatchers(db.queryExecutor)
    }

    val recipeFoodEntriesManager: (Scope) -> RecipeFoodEntriesManager = scoped { scope ->
        PlatformRecipeFoodEntriesManager(
            db,
            foodRepository,
            RecipeFoodEntriesManagerImpl()
        ).apply {
            scope.onOutOfScope = {
                this.unTrackDbChanges()
            }
        }
    }

    ///****************************Interactors*****************

    val getFoodInteractor: () -> GetFoodInteractor = {
        GetFoodInteractorImpl(dispatchers, foodRepository)
    }

    val foodActionInteractor: () -> FoodActionInteractor = {
        FoodActionInteractorImpl(dispatchers, foodRepository)
    }

    val findFoodInteractor: () -> FindFoodInteractor = {
        FindFoodInteractorImpl(dispatchers, foodRepository)
    }

    val recipeFoodActionInteractor: (Scope) -> RecipeFoodActionInteractor = scoped {
        RecipeFoodActionInteractorImpl(dispatchers, recipeFoodEntriesManager(it))
    }

    val currentMealNumberOfTheDayInteractor: () -> CurrentMealNumberOfTheDayInteractor = {
        CurrentMealNumberOfTheDayInteractorImpl(dispatchers, mealRepository)
    }

    val currentMealSaveInteractor: () -> CurrentMealSaveInteractor = {
        CurrentMealSaveInteractorImpl(dispatchers, mealRepository)
    }

    val recipeFoodEntriesDisplayInteractor: (Scope) -> RecipeFoodEntriesDisplayInteractor =
        scoped {
            RecipeFoodEntriesDisplayInteractorImpl(dispatchers, recipeFoodEntriesManager(it))
        }

    val currentMealLoadFromRecipeInteractor: (Scope) -> CurrentMealLoadFromRecipeInteractor =
        scoped {
            CurrentMealLoadFromRecipeInteractorImpl(
                dispatchers,
                recipeRepository,
                recipeFoodEntriesManager(it)
            )
        }

    val recipeSummaryInteractor: (Scope) -> RecipeSummaryInteractor = scoped {
        RecipeSummaryInteractorImpl(dispatchers, recipeFoodEntriesManager(it))
    }

    val mealJournalDetailInteractor: () -> MealJournalDetailInteractor = {
        MealJournalDetailInteractorImpl(dispatchers, mealRepository)
    }

    val mealJournalDisplayInteractor: () -> MealJournalDisplayInteractor = {
        MealJournalDisplayInteractorImpl(dispatchers, mealRepository)
    }

    val mealJournalDeleteInteractor: () -> MealJournalDeleteInteractor = {
        MealJournalDeleteInteractorImpl(dispatchers, mealRepository)
    }

    val recipeSaveInteractor: () -> RecipeSaveInteractor = {
        RecipeSaveInteractorImpl(dispatchers, recipeRepository)
    }

    val recipeDeleteInteractor: () -> RecipeDeleteInteractor = {
        RecipeDeleteInteractorImpl(dispatchers, recipeRepository)
    }

    val recipeLoadInteractor: (Scope) -> RecipeLoadInteractor = scoped {
        RecipeLoadInteractorImpl(dispatchers, recipeFoodEntriesManager(it), recipeRepository)
    }

    val recipesGetInteractor: () -> RecipesGetInteractor = {
        RecipesGetInteractorImpl(dispatchers, recipeRepository)
    }

    //******************************* view models **************************************************

    //******************************* bus events view models ***************************************
    val eventBusViewModel: () -> EventBusViewModel = {
        viewModelFromProvider(activity<MainActivity>()) {
            EventBusViewModel()
        }
    }
    //**********************************************************************************************

    val homeViewModel: () -> HomeViewModel = {
        viewModelFromProvider(fragment<HomeFragment>()) {
            val provider = fragmentProvider<HomeFragment>()
            val router = HomeViewModelRouterImpl(
                provider,
                FindFoodRouterImpl(provider),
                MealRouterImpl(provider),
                RecipeDisplayRouterImpl(provider)
            )
            HomeViewModel(router)
        }
    }

    val upsertFoodViewModel: () -> UpsertFoodViewModel = {
        with(fragment<UpsertFoodFragment>()) {
            viewModelFromProvider(this) {
                val args = UpsertFoodFragmentArgs.fromBundle(this.arguments!!)
                UpsertFoodViewModel(
                    UpsertFoodViewModel.UpsertType.decide(args.id.takeIf { it > 0 }, args.name),
                    BackRouterImpl(fragmentProvider<UpsertFoodFragment>()),
                    getFoodInteractor(),
                    foodActionInteractor()
                )
            }
        }
    }
    val findFoodViewModel: () -> FindFoodViewModel = {
        with(fragment<FindFoodFragment>()) {
            viewModelFromProvider(this) {
                val provider = fragmentProvider<FindFoodFragment>()
                FindFoodViewModel(
                    FindFoodViewModelRouterImpl(provider, UpsertFoodRouterImpl(provider)),
                    findFoodInteractor(),
                    foodActionInteractor()
                )
            }
        }
    }


    val mealViewModel: () -> MealViewModel = {
        viewModelFromProvider(fragment<MealFragment>()) {
            val scope = getScope<MealFragment>()
            val provider = fragmentProvider<MealFragment>()
            MealViewModel(
                MealRouterViewModelImpl(
                    provider,
                    FindFoodRouterImpl(provider),
                    RecipeDisplayRouterImpl(provider)
                ),
                currentMealNumberOfTheDayInteractor(),
                currentMealSaveInteractor(),
                currentMealLoadFromRecipeInteractor(scope),
                recipeSaveInteractor()
            )
        }
    }

    val mealJournalDetailViewModel: () -> MealJournalDetailViewModel = {
        viewModelFromProvider(fragment<MealJournalDetailFragment>()) {
            val mealId = MealJournalDetailFragmentArgs
                .fromBundle(arguments!!)
                .mealId
            MealJournalDetailViewModel(mealId, mealJournalDetailInteractor())
        }
    }

    val mealJournalViewModel: () -> MealJournalViewModel = {
        viewModelFromProvider(fragment<MealJournalFragment>()) {
            MealJournalViewModel(
                BackRouterImpl(fragmentProvider<MealJournalFragment>()),
                mealJournalDisplayInteractor(),
                mealJournalDeleteInteractor()
            )
        }
    }

    val recipesDisplayViewModel: () -> RecipesDisplayViewModel = {
        viewModelFromProvider(fragment<RecipesDisplayFragment>()) {
            val provider = fragmentProvider<RecipesDisplayFragment>()
            RecipesDisplayViewModel(
                RecipesDisplayViewModelRouterImpl(provider, RecipeUpsertRouterImpl(provider)),
                recipesGetInteractor(),
                recipeDeleteInteractor()
            )
        }
    }

    val recipeUpsertViewModel: () -> RecipeUpsertViewModel = {
        viewModelFromProvider(fragment<RecipeUpsertFragment>()) {
            val scope = getScope<RecipeUpsertFragment>()
            val provider = fragmentProvider<RecipeUpsertFragment>()
            RecipeUpsertViewModel(
                RecipeUpsertFragmentArgs.fromBundle(arguments!!).id,
                RecipeUpsertViewModelRouterImpl(provider, FindFoodRouterImpl(provider)),
                recipeLoadInteractor(scope),
                recipeSaveInteractor()
            )
        }
    }

    val recipeFoodsViewModel: () -> RecipeFoodsViewModel = {
        viewModelFromProvider(fragment<RecipeFoodsFragment>()) {
            val scope = getScope(this.parentFragment ?: this)
            RecipeFoodsViewModel(
                UpsertFoodRouterImpl(fragmentProvider<RecipeFoodsFragment>()),
                recipeSummaryInteractor(scope),
                recipeFoodEntriesDisplayInteractor(scope),
                recipeFoodActionInteractor(scope),
                foodActionInteractor()
            )
        }
    }

}

