package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.FoodUnchecked
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor.Request
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor.Response
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Created by Cristian Pela on 23.01.2019.
 */
interface FoodActionInteractor {

    suspend fun request(request: Request, response: (Response) -> Unit)

    object Fields {
        const val FIELD_NAME = 1
        const val FIELD_CALORIES = 2
        const val FIELD_CARBS_TOTAL = 3
        const val FIELD_CARBS_FIBER = 4
        const val FIELD_CARBS_SUGAR = 5
        const val FIELD_FATS_TOTAL = 6
        const val FIELD_FATS_SATURATED = 7
        const val FIELD_FATS_UNSATURATED = 8
        const val FIELD_PROTEINS = 9
        const val FIELD_GI = 10
    }


    sealed class Request {
        class Create(val food: FoodUnchecked) : Request()
        class Edit(val food: FoodUnchecked) : Request()
        class Delete(val food: Food) : Request()
    }

    interface Response {
        class Created(val foodId: Long) : Response
        class Edited(val food: Food) : Response
        object Deleted : Response
        sealed class Error : Throwable(), Response {
            class EmptyFields(vararg val fieldIndices: Int) : Error()
            class NegativeFields(vararg val fieldIndices: Int) : Error()
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) : Error()
            object InvalidName : Error()
            class InvalidNumberType(vararg val fieldIndices: Int) : Error()
            class Other(val throwable: Throwable) : Error()
            class Composite(vararg val errors: Error) : Error()
        }
    }

}

class FoodActionInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val foodRepository: FoodRepository) : FoodActionInteractor {

    override suspend fun request(request: Request, response: (FoodActionInteractor.Response) -> Unit) =
        supervisorScope {
            val errHandler = CoroutineExceptionHandler { _, throwable ->
                val err = when (throwable) {
                    is FoodActionInteractor.Response.Error -> throwable
                    else -> Response.Error.Other(throwable)
                }
                response(err)
            }
            launch(errHandler + dispatchers.DEFAULT) {
                var finalResponse: Response
                when (request) {
                    is Request.Create -> {
                        val checkedFood = checkFieldValidation(request.food)
                        val id = foodRepository.createSingle(checkedFood)
                        finalResponse = Response.Created(id)
                    }
                    is Request.Edit -> {
                        val checkedFood = checkFieldValidation(request.food)
                        foodRepository.edit(checkedFood)
                        finalResponse = Response.Edited(checkedFood)
                    }
                    is Request.Delete -> {
                        foodRepository.delete(request.food)
                        finalResponse = Response.Deleted
                    }
                }
                response(finalResponse)
            }
            Unit
        }


    private fun checkFieldValidation(unchecked: FoodUnchecked): Food {

        val errors = mutableListOf<Response.Error>()

        val blankFields = mutableListOf<Int>()
        if (unchecked.name.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_NAME)
        }
        if (unchecked.calories.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_CALORIES)
        }
        if (unchecked.carbohydrates.total.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_CARBS_TOTAL)
        }
        if (unchecked.carbohydrates.fiber.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_CARBS_FIBER)
        }
        if (unchecked.carbohydrates.sugar.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_CARBS_SUGAR)
        }
        if (unchecked.fat.total.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_FATS_TOTAL)
        }
        if (unchecked.fat.saturated.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_FATS_SATURATED)
        }
        if (unchecked.fat.unsaturated.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_FATS_UNSATURATED)
        }
        if (unchecked.proteins.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_PROTEINS)
        }
        if (unchecked.gi.isBlank()) {
            blankFields.add(FoodActionInteractor.Fields.FIELD_GI)
        }
        if (blankFields.isNotEmpty()) {
            errors.add(Response.Error.EmptyFields(*blankFields.toIntArray()))
        }

        //***conversion
        val badTypeFields = mutableListOf<Int>()
        val calories = unchecked.calories.trim().toIntOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_CALORIES)
        } ?: 0
        val carbTotal = unchecked.carbohydrates.total.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_CARBS_TOTAL)
        } ?: 0f
        val fibers = unchecked.carbohydrates.fiber.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_CARBS_FIBER)
        } ?: 0f
        val sugars = unchecked.carbohydrates.sugar.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_CARBS_SUGAR)
        } ?: 0f
        val fatTotal = unchecked.fat.total.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_FATS_TOTAL)
        } ?: 0f
        val saturated = unchecked.fat.saturated.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_FATS_SATURATED)
        } ?: 0f
        val unsaturated = unchecked.fat.unsaturated.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_FATS_UNSATURATED)
        } ?: 0f
        val proteins = unchecked.proteins.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_PROTEINS)
        } ?: 0f
        val gi = unchecked.gi.trim().toFloatOrNull().apply {
            if (this == null)
                badTypeFields.add(FoodActionInteractor.Fields.FIELD_GI)
        } ?: 0f
        if (badTypeFields.isNotEmpty()) {
            errors.add(Response.Error.InvalidNumberType(*badTypeFields.toIntArray()))
        }
        //***

        val food = Food(
            unchecked.id,
            unchecked.name,
            unchecked.picture,
            calories,
            Carbohydrate(carbTotal, fibers, sugars),
            Fat(fatTotal, saturated, unsaturated),
            proteins,
            gi
        )

        val negativeFields = mutableListOf<Int>()
        with(food.carbohydrates) {
            if (total < 0) {
                negativeFields.add(4)
            }
            if (fiber < 0) {
                negativeFields.add(5)
            }
            if (sugar < 0) {
                negativeFields.add(6)
            }
        }

        with(food.fat) {
            if (total < 0) {
                negativeFields.add(7)
            }
            if (saturated < 0) {
                negativeFields.add(8)
            }
            if (unsaturated < 0) {
                negativeFields.add(9)
            }
        }
        if (food.proteins < 0) {
            negativeFields.add(10)
        }

        if (negativeFields.isNotEmpty()) {
            errors.add(Response.Error.NegativeFields(*negativeFields.toIntArray()))
        }

        food.gi.let {
            if (it !in 0..100) {
                errors.add(Response.Error.GIOutOfBounds(it.toInt()))
            }
        }

        if (errors.isNotEmpty()) {
            if (errors.size == 1) {
                throw errors.first()
            } else {
                throw Response.Error.Composite(*errors.toTypedArray())
            }
        }

        return food.let {
            //since most of the packages have total and saturated fats,
            // adjust fats unsaturated if 0 as a diff between total and unsaturated
            if (it.fat.unsaturated == 0f) {
                it.copy(fat = it.fat.copy(unsaturated = it.fat.total - it.fat.saturated))
            } else {
                it
            }
        }
    }
}

