@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.crskdev.mealcalculator

import android.app.Application
import android.content.Context
import com.crskdev.mealcalculator.data.FoodRepositoryImpl
import com.crskdev.mealcalculator.data.MealRepositoryImpl
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.*
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.platform.PlatformCurrentMealEntryManager
import com.crskdev.mealcalculator.platform.PlatformGatewayDispatchers
import com.crskdev.mealcalculator.presentation.common.SelectedFoodViewModel
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModel
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel
import com.crskdev.mealcalculator.presentation.meal.MealJournalDetailViewModel
import com.crskdev.mealcalculator.presentation.meal.MealJournalViewModel
import com.crskdev.mealcalculator.presentation.meal.MealViewModel
import com.crskdev.mealcalculator.ui.common.di.BaseDependencyGraph
import com.crskdev.mealcalculator.ui.food.FindFoodFragment
import com.crskdev.mealcalculator.ui.food.UpsertFoodFragment
import com.crskdev.mealcalculator.ui.food.UpsertFoodFragmentArgs
import com.crskdev.mealcalculator.ui.meal.*
import com.crskdev.mealcalculator.utils.viewModelFromProvider

/**
 * Created by Cristian Pela on 28.01.2019.
 */
class MealCalculatorApplication : Application() {

    lateinit var dependencyGraph: DependencyGraph

    override fun onCreate() {
        super.onCreate()
        dependencyGraph = DependencyGraph(this)
    }
}

fun Context.dependencyGraph() =
    this.applicationContext.cast<MealCalculatorApplication>().dependencyGraph

class DependencyGraph(context: Context) : BaseDependencyGraph(context) {

    val db: MealCalculatorDatabase by lazy {
        MealCalculatorDatabase.persistent(context) {
            foodRepository.runTransaction {
                create(
                    Food(
                        0,
                        "Lidl Oat Flakes Fine",
                        null,
                        372,
                        Carbohydrate(58.7f, 10f, 0.7f),
                        Fat(7f, 1.3f, 5.7f),
                        13.5f,
                        55f
                    ),
                    Food(
                        0,
                        "Auchan Naut Granoro",
                        null,
                        72,
                        Carbohydrate(8.6f, 4.5f, 0f),
                        Fat(1.3f, 0.1f, 1.2f),
                        4.3f,
                        28f
                    ),
                    Food(
                        0,
                        "Kaufland Migdale Macinate Mandelin",
                        null,
                        627,
                        Carbohydrate(4.3f, 9.2f, 4.2f),
                        Fat(55f, 4.7f, 50.3f),
                        24f,
                        0f
                    ),
                    Food(
                        0,
                        "Sanovita Seminte de In",
                        null,
                        525,
                        Carbohydrate(10.4f, 26.9f, 0.4f),
                        Fat(41f, 2.8f, 0f),
                        24f,
                        0f
                    )
                )
            }
        }
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepositoryImpl(db)
    }

    val mealRepository: MealRepository by lazy {
        MealRepositoryImpl(db)
    }

    val dispatchers: GatewayDispatchers = PlatformGatewayDispatchers

    val currentMealEntryManager: () -> CurrentMealEntryManager =
        withinFragmentScope<MealFragment, PlatformCurrentMealEntryManager> {
            PlatformCurrentMealEntryManager(
                activity(),
                db,
                foodRepository,
                CurrentMealEntryManagerImpl()
            )
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

    val currentMealActionInteractor: () -> CurrentMealActionInteractor = {
        CurrenMealActionInteractorImpl(dispatchers, mealRepository, currentMealEntryManager())
    }

    val currentMealEntryDisplayInteractor: () -> CurrentMealEntryDisplayInteractor = {
        CurrentMealEntryDisplayInteractorImpl(dispatchers, currentMealEntryManager())
    }

    val currentMealDisplayInteractor: () -> CurrentMealDisplayInteractor = {
        CurrentMealDisplayInteractorImpl(dispatchers, mealRepository, currentMealEntryManager())
    }

    val mealJournalDetailInteractor: () -> MealJournalDetailInteractor = {
        MealJournalDetailInteractorImpl(dispatchers, mealRepository)
    }

    val mealJournalDisplayInteractor: () -> MealJournalDisplayInteractor = {
        MealJournalDisplayInteractorImpl(dispatchers, mealRepository)
    }

    //******************************* view models *************************************************
    val upsertFoodViewModel: () -> UpsertFoodViewModel = {
        with(fragment<UpsertFoodFragment>()) {
            viewModelFromProvider(this) {
                val args = UpsertFoodFragmentArgs.fromBundle(this.arguments!!)
                UpsertFoodViewModel(
                    UpsertFoodViewModel.UpsertType.decide(args.id.takeIf { it > 0 }, args.name),
                    getFoodInteractor(),
                    foodActionInteractor()
                )
            }
        }
    }
    val findFoodViewModel: () -> FindFoodViewModel = {
        with(fragment<FindFoodFragment>()) {
            viewModelFromProvider(this) {
                FindFoodViewModel(findFoodInteractor(), foodActionInteractor())
            }
        }
    }

    val selectedFoodViewModel: () -> SelectedFoodViewModel = {
        viewModelFromProvider(activity<MainActivity>()) {
            SelectedFoodViewModel()
        }
    }

    val mealViewModel: () -> MealViewModel = {
        viewModelFromProvider(fragment<MealFragment>()) {
            MealViewModel(
                currentMealDisplayInteractor(),
                currentMealEntryDisplayInteractor(),
                currentMealActionInteractor(),
                foodActionInteractor()
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
            MealJournalViewModel(mealJournalDisplayInteractor())
        }
    }

}

