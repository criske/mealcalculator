package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.Main
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.testutils.cancelDelayed
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Cristian Pela on 24.01.2019.
 */
@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
@ObsoleteCoroutinesApi
class SearchFoodInteractorImplTest {

    @MockK
    lateinit var foodRepository: FoodRepository

    lateinit var interactor: SearchFoodInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        interactor = SearchFoodInteractorImpl(
            Main.GatewayDispatchersImpl, foodRepository
        )
    }

    @Test
    fun `should return a search list when query is valid`() {
        val list = listOf(
            Food(
                -1,
                "",
                null,
                52,
                Carbohydrate(14f, 2.4f, 10f),
                Fat(0.2f, 0f, 0.1f),
                0.3f,
                38
            )
        )
        coEvery { foodRepository.search(any()) } returns list

        runBlocking {
            actor<String> {
                interactor.request(channel) {
                    assertTrue(it is SearchFoodInteractor.Response.SearchList)
                    assertEquals(list, (it as SearchFoodInteractor.Response.SearchList).list)
                    cancel()
                }
            }.send("foo")
        }
    }

    @Test
    fun `should return an empty list when query not match and possibility to create a new food`() {
        val list = emptyList<Food>()
        coEvery { foodRepository.search(any()) } returns list

        runBlocking {
            val responses = mutableListOf<SearchFoodInteractor.Response>()
            actor<String> {
                interactor.request(channel) {
                    responses.add(it)
                    launch { cancelDelayed() }
                }
            }.send("foo")
            assertEquals(
                listOf(
                    SearchFoodInteractor.Response.SearchList::class,
                    SearchFoodInteractor.Response.CreateNewFoodWithQueryNameWhenEmpty::class
                ),
                responses.map { it::class }
            )
        }
    }

    @Test
    fun `should return an empty list when query not match and no possibility to create a new food`() {
        val list = emptyList<Food>()
        coEvery { foodRepository.search(any()) } returns list

        runBlocking {
            val responses = mutableListOf<SearchFoodInteractor.Response>()
            actor<String> {
                interactor.request(channel) {
                    responses.add(it)
                    launch { cancelDelayed() }
                }
            }.send("fo")
            assertEquals(
                listOf(
                    SearchFoodInteractor.Response.SearchList::class
                ),
                responses.map { it::class }
            )
        }
    }

}

