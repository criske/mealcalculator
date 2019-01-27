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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.data.FoodRepositoryImpl
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractorImpl
import com.crskdev.mealcalculator.domain.interactors.GetFoodInteractorImpl
import com.crskdev.mealcalculator.platform.PictureToStringConverterImpl
import com.crskdev.mealcalculator.platform.PlatformGatewayDispatchers
import com.crskdev.mealcalculator.presentation.common.entities.CarbohydrateVM
import com.crskdev.mealcalculator.presentation.common.entities.FatVM
import com.crskdev.mealcalculator.presentation.common.entities.FoodVM
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
import com.crskdev.mealcalculator.utils.viewModelFromProvider
import kotlinx.android.synthetic.main.fragment_upsert_food.*
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory


/**
 * A simple [Fragment] subclass.
 *
 */
class UpsertFoodFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 10000
    }

    lateinit var viewModel: UpsertFoodViewModel

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data?.extras?.get("data")?.cast<Bitmap>()?.also {
                viewModel.setPicture(
                    it, context!!
                        .resources
                        .getDimensionPixelSize(R.dimen.food_image_size)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelFromProvider(this) {
            val db = MealCalculatorDatabase.inMemory(this.context!!)
            val foodRepository = FoodRepositoryImpl(db)
            val getFoodInteractor =
                GetFoodInteractorImpl(PlatformGatewayDispatchers, foodRepository)
            val foodActionInteractor =
                FoodActionInteractorImpl(PlatformGatewayDispatchers, foodRepository)

            UpsertFoodViewModel(
                UpsertFoodViewModel.UpsertType.decide(null, "Oatmeal"),
                getFoodInteractor,
                foodActionInteractor,
                PictureToStringConverterImpl(),
                PlatformGatewayDispatchers
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upsert_food, container, false)
    }

    private fun convertStrToBitmap(base64Str: String): Drawable{
        val decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return RoundedBitmapDrawableFactory.create(resources,
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)).apply {
            cornerRadius = 5f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnUpsertFoodChangeImage.setOnClickListener {
            if (ContextCompat
                    .checkSelfPermission(it.context, Manifest.permission.CAMERA)
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

        buttonUpsertFood.setOnClickListener {
            viewModel.upsert(
                FoodVM(
                    0,
                    editInputUpsertFoodName.editText?.text?.toString() ?: "",
                    null,
                    editInputUpsertFoodCalories.editText?.text?.toString() ?: "",
                    CarbohydrateVM(
                        editInputUpsertFoodCarbsTotal.editText?.text?.toString() ?: "",
                        editInputUpsertFoodCarbFiber.editText?.text?.toString() ?: "",
                        editInputUpsertFoodCarbSugar.editText?.text?.toString() ?: ""
                    ),
                    FatVM(
                        editInputUpsertFoodFatTotal.editText?.text?.toString() ?: "",
                        editInputUpsertFoodFatSaturated.editText?.text?.toString() ?: "",
                        editInputUpsertFoodFatUnsaturated.editText?.text?.toString() ?: ""
                    ),
                    editInputUpsertFoodProteins.editText?.text?.toString() ?: "",
                    editInputUpsertFoodGI.editText?.text?.toString() ?: ""
                )
            )
        }

        with(viewModel) {
            val thisFragment = this@UpsertFoodFragment
            retainedModelLiveData.observe(thisFragment, Observer {
                editInputUpsertFoodName.editText?.setText(it.name)
                editInputUpsertFoodCalories.editText?.setText(it.calories)
                editInputUpsertFoodCarbsTotal.editText?.setText(it.carbohydrates.total)
                editInputUpsertFoodCarbFiber.editText?.setText(it.carbohydrates.fiber)
                editInputUpsertFoodCarbSugar.editText?.setText(it.carbohydrates.sugar)
                editInputUpsertFoodFatTotal.editText?.setText(it.fat.total)
                editInputUpsertFoodFatUnsaturated.editText?.setText(it.fat.unsaturated)
                editInputUpsertFoodFatSaturated.editText?.setText(it.fat.saturated)
                editInputUpsertFoodProteins.editText?.setText(it.proteins)
                editInputUpsertFoodGI.editText?.setText(it.gi)
                if(it.picture == null){
                    imageUpsertFood.setImageResource(R.drawable.ic_launcher_background)
                }else {
                    imageUpsertFood.setImageDrawable(convertStrToBitmap(it.picture as String))
                }
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
                    Toast.makeText(context, textErrors, Toast.LENGTH_LONG).show()
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



