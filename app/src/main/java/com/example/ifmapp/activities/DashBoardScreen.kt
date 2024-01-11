package com.example.ifmapp.activities

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityDashBoardScreenBinding

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_screen)
binding = DataBindingUtil.setContentView(this,R.layout.activity_dash_board_screen)
        binding.forgotPin.paintFlags = binding.forgotPin.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val editTexts = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)

        for (i in 0 until editTexts.size) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Implement your logic here if needed
                }
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // If backspace is pressed, move the focus to the previous EditText
                    if (i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                    true
                } else {
                    false
                }
            }
        }
        setupOtpEditTextListeners()

    }


    private fun setupOtpEditTextListeners() {
        binding.otp1.addTextChangedListener(createOtpTextWatcher(binding.otp2))
        binding.otp2.addTextChangedListener(createOtpTextWatcher(binding.otp3))
        binding.otp3.addTextChangedListener(createOtpTextWatcher(binding.otp4))
        binding.otp4.addTextChangedListener(createOtpTextWatcher(null))
    }

    private fun createOtpTextWatcher(nextEditText: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    nextEditText?.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

}