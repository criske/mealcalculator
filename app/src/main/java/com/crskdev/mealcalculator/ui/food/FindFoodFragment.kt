package com.crskdev.mealcalculator.ui.food


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModel
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import kotlinx.android.synthetic.main.fragment_find_food.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FindFoodFragment : DiFragment() {

    private val viewModel: FindFoodViewModel by lazy {
        di.findFoodViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editFoodsSearch.doAfterTextChanged { e ->
            e?.also {
                viewModel.search(it.toString())
            }
        }
        with(recyclerFoodsSearch) {
            adapter = FindFoodAdapter(LayoutInflater.from(context))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.foodsLiveData.observe(this, Observer {
            if (it is PagedList) {
                recyclerFoodsSearch.adapter?.cast<FindFoodAdapter>()?.submitList(it)
            } else {
                Toast.makeText(
                    context,
                    "Result list must be a Paged List. Current ${it::class}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }


}
