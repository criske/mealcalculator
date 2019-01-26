package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Created by Cristian Pela on 23.01.2019.
 */
interface FoodActionInteractor {

    suspend fun request(request: Request, response: (Response) -> Unit)


    sealed class Request {
        class Create(val food: Food) : Request()
        class Edit(val food: Food) : Request()
        class Delete(val food: Food) : Request()
    }

    interface Response {
        class Created(val foodId: Long) : Response
        object Edited : Response
        object Deleted : Response
        sealed class Error : Throwable(), Response {
            class EmptyFields(vararg val fieldIndices: Int) : Error()
            class NegativeFields(vararg val fieldIndices: Int) : Error()
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) : Error()
            object InvalidName : Error()
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
                        checkFieldValidation(request.food)
                        val id = foodRepository.create(request.food)
                        finalResponse = Response.Created(id)
                    }
                    is Request.Edit -> {
                        checkFieldValidation(request.food)
                        foodRepository.edit(request.food)
                        finalResponse = Response.Edited
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


    private fun checkFieldValidation(food: Food) {

        val errors = mutableListOf<Response.Error>()

        if (food.name.isBlank()) {
            errors.add(Response.Error.EmptyFields(1))
        }

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

        food.gi?.let {
            if (it !in 0..100) {
                errors.add(Response.Error.GIOutOfBounds(it))
            }
        }

        if (errors.isNotEmpty()) {
            if (errors.size == 1) {
                throw errors.first()
            } else {
                throw Response.Error.Composite(*errors.toTypedArray())
            }
        }
    }
}

