@file:Suppress("unused")

package com.crskdev.mealcalculator.domain

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractorImpl
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class Main {

    companion object {
        @JvmStatic
        fun main(@Suppress("UnusedMainParameter") args: Array<String>) {
            val apple = Food(
                -1,
                "",
                null,
                52,
                Carbohydrate(14f, 2.4f, 10f),
                Fat(0.2f, 0f, 0.1f),
                0.3f,
                38
            )

            val interactor = FoodActionInteractorImpl(GatewayDispatchersImpl, FoodRepositoryImpl())

            runBlocking {
                interactor.request(FoodActionInteractor.Request.Create(apple)) {
                    when(it){
                        is FoodActionInteractor.Response.Error.Composite ->{
                            it.errors.forEach {
                                println(it)
                            }
                        }
                        else -> println(it)
                    }
                }
            }

        }

    }


    internal object GatewayDispatchersImpl : GatewayDispatchers {
        override val IO: CoroutineDispatcher
            get() = Dispatchers.Unconfined
        override val DEFAULT: CoroutineDispatcher
            get() = Dispatchers.Unconfined
        override val MAIN: CoroutineDispatcher
            get() = Dispatchers.Unconfined
        override val UNCONFINED: CoroutineDispatcher
            get() = Dispatchers.Unconfined

        override fun custom(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    internal class FoodRepositoryImpl : FoodRepository {

        override suspend fun search(like: String): List<Food> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun create(food: Food) {}

        override fun edit(food: Food) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun delete(id: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun runTransaction(block: FoodRepository.() -> Unit) {
            this.block()
        }
    }
}
