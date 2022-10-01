package dfilipovi.darkoapp

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dfilipovi.darkoapp.database.HealthContract
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

	private val dateFormatter: DateFormat = SimpleDateFormat("dd.MM.yyyy.")

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add)
		initializeDatePicker()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = this.menuInflater
		inflater.inflate(R.menu.add_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.task_save -> {
				save()
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun save() {
		val date: String = findViewById<EditText>(R.id.input_date).text.toString()
		val weight: String = findViewById<EditText>(R.id.input_weight).text.toString()
		val sugar: String = findViewById<EditText>(R.id.input_sugar).text.toString()
		val bloodPressure1: String = findViewById<EditText>(R.id.input_blood_pressure_1).text.toString()
		val bloodPressure2: String = findViewById<EditText>(R.id.input_blood_pressure_2).text.toString()
		val bloodPressure3: String = findViewById<EditText>(R.id.input_blood_pressure_3).text.toString()

		val dbHelper = HealthContract.HealthDatabaseHelper(this)
		val db = dbHelper.writableDatabase

		val values = ContentValues().apply {
			put(HealthContract.HealthEntry.ATTR_DATE, date)
			put(HealthContract.HealthEntry.ATTR_WEIGHT, weight)
			put(HealthContract.HealthEntry.ATTR_SUGAR, sugar)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_1, bloodPressure1)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_2, bloodPressure2)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_3, bloodPressure3)
		}

		val id: Long? = db?.insert(HealthContract.HealthEntry.ENTITY_NAME, null, values)
		if (id != null && !id.equals(-1)) {
			Toast.makeText(this, "Uspješno spremljeno.", Toast.LENGTH_SHORT).show()
			finish()
		} else {
			Toast.makeText(this, "Greška u spremanju.", Toast.LENGTH_SHORT).show()
		}
	}

	private fun initializeDatePicker() {
		val txtDate: EditText = findViewById(R.id.input_date)
		txtDate.setOnClickListener {
			val text: String = txtDate.text.toString()
			val calendar: Calendar = Calendar.getInstance()
			if (!text.isBlank()) {
				val parsedDate = dateFormatter.parse(text)
				calendar.timeInMillis = parsedDate.time
			}

			DatePickerDialog(
				this,
				{ _, year, month, dayOfMonth -> txtDate.setText("%02d.%02d.%02d.".format(dayOfMonth, month + 1, year)) },
				calendar[Calendar.YEAR],
				calendar[Calendar.MONTH],
				calendar[Calendar.DAY_OF_MONTH]
			).show()
		}
	}

}