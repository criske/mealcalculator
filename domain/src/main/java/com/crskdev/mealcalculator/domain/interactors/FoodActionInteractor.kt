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
        class Delete(val id: Int) : Request()
    }

    interface Response {
        object OK : Response
        sealed class Error : Throwable(), Response {
            class EmptyFields(vararg val fieldIndices: Int) : Error()
            class NegativeFields(vararg val fieldIndices: Int) : Error()
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) : Error()
            object InvalidName : Error()
            class Other(throwable: Throwable) : Error()
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
                when (request) {
                    is Request.Create -> {
                        checkFieldValidation(request.food)
                        foodRepository.create(request.food)
                    }
                    is Request.Edit -> {
                        checkFieldValidation(request.food)
                        foodRepository.edit(request.food)
                    }
                    is Request.Delete -> foodRepository.delete(request.id)
                }
                response(Response.OK)
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

