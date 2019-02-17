package com.crskdev.mealcalculator.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Recipe
import kotlinx.android.synthetic.main.item_recipe.view.*

/**
 * Created by Cristian Pela on 13.02.2019.
 */
class RecipesAdapter(private val inflater: LayoutInflater,
                     private val action: (RecipesAdapter.Action) -> Unit) :
    ListAdapter<Recipe, RecipeVH>(
        object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
                oldItem == newItem

        }) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeVH =
        RecipeVH(inflater.inflate(R.layout.item_recipe, parent, false), action)

    override fun onBindViewHolder(holder: RecipeVH, position: Int) {
        holder.bind(getItem(position))
    }

    sealed class Action(val recipe: Recipe) {
        class Select(recipe: Recipe) : Action(recipe)
        class Edit(recipe: Recipe) : Action(recipe)
    }
}


class RecipeVH(v: View, action: (RecipesAdapter.Action) -> Unit) : RecyclerView.ViewHolder(v) {

    private var recipe: Recipe? = null

    init {
        itemView.setOnClickListener {
            recipe?.also {
                action(RecipesAdapter.Action.Select(it))
            }
        }
        itemView.setOnLongClickListener {
            recipe?.also {
                action(RecipesAdapter.Action.Edit(it))
            }
            true
        }
    }

    fun bind(recipe: Recipe) {
        this.recipe = recipe
        with(itemView) {
            textItemRecipe.text = recipe.name
            textItemRecipeFoodNames.text = recipe.foodNames
        }
    }

    fun clear() {
        with(itemView) {
            textItemRecipe.text = null
            textItemRecipeFoodNames.text = null
        }
    }

}