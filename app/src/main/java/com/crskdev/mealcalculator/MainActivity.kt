package com.crskdev.mealcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.crskdev.mealcalculator.domain.TestDomain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hello.text = TestDomain.hello

    }
}
