package com.crskdev.mealcalculator.ui.food


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.entities.CarbohydrateVM
import com.crskdev.mealcalculator.presentation.common.entities.FatVM
import com.crskdev.mealcalculator.presentation.common.entities.FoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toFloatFormat
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_CALORIES
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_CARBS_FIBER
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_CARBS_SUGAR
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_CARBS_TOTAL
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_FATS_SATURATED
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_FATS_TOTAL
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_FATS_UNSATURATED
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_GI
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_NAME
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Companion.FIELD_PROTEINS
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.ProjectImageUtils
import com.crskdev.mealcalculator.utils.showSimpleToast
import kotlinx.android.synthetic.main.fragment_upsert_food.*


/**
 * A simple [Fragment] subclass.
 *
 */
class UpsertFoodFragment : DiFragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 10000
    }

    private val viewModel: UpsertFoodViewModel by lazy {
        di.upsertFoodViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data?.extras?.get("data")?.cast<Bitmap>()?.also {
                val size = context!!
                    .resources
                    .getDimensionPixelSize(R.dimen.food_image_size)
                val bitmapBase64 = ProjectImageUtils.convertBitmapToBase64String(it, size)
                viewModel.upsertWithOutSave(extractFoodVM().copy(picture = bitmapBase64))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upsert_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(toolbarUpsertFood) {
            inflateMenu(R.menu.menu_food_upsert)
            setNavigationOnClickListener {
                viewModel.routeBack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_food_upsert_save -> {
                        viewModel.upsert(extractFoodVM())
                    }
                }
                true
            }
        }

        btnUpsertFoodChangeImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(it.context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1337)
            } else {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        with(viewModel) {
            val thisFragment = this@UpsertFoodFragment
            retainedModelLiveData.observe(thisFragment, Observer {
                editInputUpsertFoodName.editText?.setText(it.name)
                editInputUpsertFoodCalories.editText?.setText(it.calories)
                editInputUpsertFoodCarbsTotal.editText?.setText(it.carbohydrates.total())
                editInputUpsertFoodCarbFiber.editText?.setText(it.carbohydrates.fiber())
                editInputUpsertFoodCarbSugar.editText?.setText(it.carbohydrates.sugar())
                editInputUpsertFoodFatTotal.editText?.setText(it.fat.total())
                editInputUpsertFoodFatUnsaturated.editText?.setText(it.fat.unsaturated())
                editInputUpsertFoodFatSaturated.editText?.setText(it.fat.saturated())
                editInputUpsertFoodProteins.editText?.setText(it.proteins())
                editInputUpsertFoodGI.editText?.setText(it.gi())
                imageUpsertFood.setImageDrawable(
                    it.picture?.let { p ->
                        ProjectImageUtils.convertStrToRoundedDrawable(resources, p)
                    }
                )
            })
            errLiveData.observe(thisFragment, Observer {
                for (field in UpsertFoodViewModel.FIELD_NAME..UpsertFoodViewModel.FIELD_GI) {
                    it.get(field)?.let { errors ->
                        val txtErrors = buildString {
                            errors.forEach { e ->
                                when (e) {
                                    UpsertFoodViewModel.Error.FieldError.Empty ->
                                        append("*Empty Field\n")
                                    UpsertFoodViewModel.Error.FieldError.Negative ->
                                        append("*Negative Value Not Allowed\n")
                                    UpsertFoodViewModel.Error.FieldError.InvalidName ->
                                        append("*Invalid Name\n")
                                    is UpsertFoodViewModel.Error.FieldError.GIOutOfBounds ->
                                        append("*GI must be between ${e.min} and ${e.max}\n")
                                    UpsertFoodViewModel.Error.FieldError.NumberType ->
                                        append("*Must be a number\n")
                                    else -> {
                                    }
                                }
                            }
                        }
                        getFieldEditText(field)?.error = txtErrors
                    }
                }
                //handle other errors
                it.get(UpsertFoodViewModel.FIELD_NONE)?.let {
                    val textErrors = buildString {
                        it.forEach { e ->
                            if (e is UpsertFoodViewModel.Error.Other) {
                                append("${e.throwable.message}\n")
                            } else if (e is UpsertFoodViewModel.Error.FoodNotFound) {
                                append("Food with id ${e.id} not found.\n")
                            }
                        }
                    }
                    context?.showSimpleToast(textErrors)
                }
            })
            clearErrorsLiveData.observe(thisFragment, Observer {
                editInputUpsertFoodName.editText?.error = null
                editInputUpsertFoodCalories.editText?.error = null
                editInputUpsertFoodCarbsTotal.editText?.error = null
                editInputUpsertFoodCarbFiber.editText?.error = null
                editInputUpsertFoodCarbSugar.editText?.error = null
                editInputUpsertFoodFatTotal.editText?.error = null
                editInputUpsertFoodFatUnsaturated.editText?.error = null
                editInputUpsertFoodFatSaturated.editText?.error = null
                editInputUpsertFoodGI.editText?.error = null
            })
        }
    }

    private fun extractFoodVM(): FoodVM {
        return FoodVM(
            0,
            editInputUpsertFoodName.editText?.text?.toString() ?: "",
            null,
            editInputUpsertFoodCalories.editText?.text?.toString() ?: "0",
            CarbohydrateVM(
                editInputUpsertFoodCarbsTotal.editText?.text?.toString().toFloatFormat(),
                editInputUpsertFoodCarbFiber.editText?.text?.toString().toFloatFormat(),
                editInputUpsertFoodCarbSugar.editText?.text?.toString().toFloatFormat()
            ),
            FatVM(
                editInputUpsertFoodFatTotal.editText?.text?.toString().toFloatFormat(),
                editInputUpsertFoodFatSaturated.editText?.text?.toString().toFloatFormat(),
                editInputUpsertFoodFatUnsaturated.editText?.text?.toString().toFloatFormat()
            ),
            editInputUpsertFoodProteins.editText?.text?.toString().toFloatFormat(),
            editInputUpsertFoodGI.editText?.text?.toString().toFloatFormat()
        )
    }

    private fun getFieldEditText(field: Int): EditText? {
        return when (field) {
            FIELD_NAME -> editInputUpsertFoodName.editText
            FIELD_CALORIES -> editInputUpsertFoodCalories.editText
            FIELD_CARBS_TOTAL -> editInputUpsertFoodCarbsTotal.editText
            FIELD_CARBS_FIBER -> editInputUpsertFoodCarbFiber.editText
            FIELD_CARBS_SUGAR -> editInputUpsertFoodCarbSugar.editText
            FIELD_FATS_TOTAL -> editInputUpsertFoodFatTotal.editText
            FIELD_FATS_SATURATED -> editInputUpsertFoodFatSaturated.editText
            FIELD_FATS_UNSATURATED -> editInputUpsertFoodFatUnsaturated.editText
            FIELD_PROTEINS -> editInputUpsertFoodProteins.editText
            FIELD_GI -> editInputUpsertFoodGI.editText
            else -> null
        }
    }


}



