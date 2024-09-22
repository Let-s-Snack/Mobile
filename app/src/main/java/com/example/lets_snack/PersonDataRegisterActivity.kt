package com.example.lets_snack

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.core.content.ContextCompat
import com.example.lets_snack.R
import com.example.lets_snack.databinding.ActivityPersonDataRegisterBinding
import java.util.Calendar

class PersonDataRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonDataRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDataRegisterBinding.inflate(getLayoutInflater())
        setContentView(binding.root)
           var genderInput = binding.genderInputText
        var genders = arrayOf("feminino","masculino","outro","prefiro não dizer")
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, R.id.dropdownText, genders)
        genderInput.setAdapter(adapter)

        genderInput.setOnClickListener {
            genderInput.dropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT  // ou um valor específico em pixels
            genderInput.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT  // ou um valor específico em pixels
            genderInput.showDropDown()
            genderInput.dropDownVerticalOffset = 10  // Ajuste a distância do campo
        }

        // Adiciona um listener para lidar com a seleção
        genderInput.setOnItemClickListener { parent, view, position, id ->
            val selectedGender = parent.getItemAtPosition(position).toString()
            // Faça algo com a opção selecionada, se necessário
        }

        binding.textInputLayout4.setEndIconOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, R.style.CustomDatePickerDialog, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            binding.dateOfBirthInput.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.setOnShowListener {
            val positiveButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negativeButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
        }

        datePickerDialog.show()
    }

}