package com.crskdev.mealcalculator.ui.recipe


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.onItemSwipe
import com.crskdev.mealcalculator.utils.showSimpleYesNoDialog
import kotlinx.android.synthetic.main.fragment_recipes_display.*

class RecipesDisplayFragment : DiFragment() {

    private val viewModel by lazy {
        di.recipesDisplayViewModel()
    }

    private val eventBusViewModel by lazy {
        di.eventBusViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarRecipesDisplay) {
            inflateMenu(R.menu.menu_add)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_add ->
                        findNavController().navigate(
                            RecipesDisplayFragmentDirections
                                .actionRecipesDisplayFragmentToRecipeUpsertFragment(0)
                        )

                }
                true
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        with(recyclerRecipesDisplay) {
            adapter = RecipesAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is RecipesAdapter.Action.Select -> viewModel.select(it.recipe)
                    is RecipesAdapter.Action.Edit -> findNavController().navigate(
                        RecipesDisplayFragmentDirections
                            .actionRecipesDisplayFragmentToRecipeUpsertFragment(it.recipe.id)
                    )
                }
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            onItemSwipe { vh, _ ->
                postDelayed(100) {
                    this@RecipesDisplayFragment
                        .context
                        ?.showSimpleYesNoDialog("Warning", "Permanently delete this recipe?") {
                            if (DialogInterface.BUTTON_POSITIVE == it) {
                                viewModel.delete(vh.adapterPosition)
                            } else {
                                //revert the swipe animation
                                recyclerRecipesDisplay.adapter?.notifyItemChanged(vh.adapterPosition)
                            }
                        }
                }
            }
        }
        viewModel.recipesLiveData.observe(viewLifecycleOwner, Observer {
            recyclerRecipesDisplay.adapter?.cast<RecipesAdapter>()?.submitList(it)
        })
        viewModel.selectedRecipeLiveData.observe(viewLifecycleOwner, Observer {
            val args = RecipesDisplayFragmentArgs.fromBundle(arguments!!)
            if (args.code != EventBusViewModel.Event.NO_CODE) {
                eventBusViewModel.sendEvent(EventBusViewModel.Event(args.code, it))
                findNavController().popBackStack()
            }
        })

    }
}
