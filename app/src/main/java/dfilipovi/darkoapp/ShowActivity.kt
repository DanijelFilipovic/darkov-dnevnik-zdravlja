package dfilipovi.darkoapp

import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
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

class ShowActivity : AppCompatActivity() {

	private var mEntityId: Int = -1;
	private val dateFormatter: DateFormat = SimpleDateFormat("dd.MM.yyyy.")

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_show)
		initialize()
		initializeDatePicker()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = this.menuInflater
		inflater.inflate(R.menu.show_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.task_update -> {
				val numberOfRowsAffected = updateEntity()
				if (numberOfRowsAffected > 0)
					Toast.makeText(this, "Ažurirano", Toast.LENGTH_SHORT).show()
				else
					Toast.makeText(this, "Ažuriranje nije provedeno.", Toast.LENGTH_SHORT).show()
				return true
			}
			R.id.task_delete -> {
				val numberOfRowsAffected = deleteEntity()
				if (numberOfRowsAffected > 0) {
					Toast.makeText(this, "Izbrisano", Toast.LENGTH_SHORT).show()
					finish()
				}
				else
					Toast.makeText(this, "Brisanje nije provedeno.", Toast.LENGTH_SHORT).show()
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun initialize() {
		this.mEntityId = this.intent.extras?.get("id") as Int
		if (this.mEntityId != -1) {
			queryForEntity(this.mEntityId)?.use {
				it.moveToNext()
				findViewById<EditText>(R.id.show_date).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_DATE)))
				findViewById<EditText>(R.id.show_weight).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_WEIGHT)))
				findViewById<EditText>(R.id.show_sugar).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_SUGAR)))
				findViewById<EditText>(R.id.show_blood_pressure_1).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_1)))
				findViewById<EditText>(R.id.show_blood_pressure_2).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_2)))
				findViewById<EditText>(R.id.show_blood_pressure_3).setText(it.getString(it.getColumnIndex(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_3)))
			}
		}
	}

	private fun initializeDatePicker() {
		val txtDate: EditText = findViewById(R.id.show_date)
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

	private fun queryForEntity(id: Int): Cursor? {
		val dbHelper = HealthContract.HealthDatabaseHelper(this)
		val database = dbHelper.readableDatabase

		val projection = arrayOf(
			BaseColumns._ID,
			HealthContract.HealthEntry.ATTR_DATE,
			HealthContract.HealthEntry.ATTR_WEIGHT,
			HealthContract.HealthEntry.ATTR_SUGAR,
			HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_1,
			HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_2,
			HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_3
		)
		val selection = "${BaseColumns._ID} = ?"
		val selectionArgs = arrayOf("$id")

		val cursor = database.query(HealthContract.HealthEntry.ENTITY_NAME, projection, selection, selectionArgs, null, null, null)
		return cursor
	}

	private fun updateEntity(): Int {
		val date: String = findViewById<EditText>(R.id.show_date).text.toString()
		val weight: String = findViewById<EditText>(R.id.show_weight).text.toString()
		val sugar: String = findViewById<EditText>(R.id.show_sugar).text.toString()
		val bloodPressure1: String = findViewById<EditText>(R.id.show_blood_pressure_1).text.toString()
		val bloodPressure2: String = findViewById<EditText>(R.id.show_blood_pressure_2).text.toString()
		val bloodPressure3: String = findViewById<EditText>(R.id.show_blood_pressure_3).text.toString()

		val dbHelper = HealthContract.HealthDatabaseHelper(this)
		val database = dbHelper.writableDatabase

		val values = ContentValues().apply {
			put(HealthContract.HealthEntry.ATTR_DATE, date)
			put(HealthContract.HealthEntry.ATTR_WEIGHT, weight)
			put(HealthContract.HealthEntry.ATTR_SUGAR, sugar)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_1, bloodPressure1)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_2, bloodPressure2)
			put(HealthContract.HealthEntry.ATTR_BLOOD_PRESSURE_3, bloodPressure3)
		}
		val whereClause = "${BaseColumns._ID} = ?"
		val whereArgs = arrayOf("${this.mEntityId}")

		return database.update(HealthContract.HealthEntry.ENTITY_NAME, values, whereClause, whereArgs)
	}

	private fun deleteEntity(): Int {
		val dbHelper = HealthContract.HealthDatabaseHelper(this)
		val database = dbHelper.writableDatabase

		val whereClause = "${BaseColumns._ID} = ?"
		val whereArgs = arrayOf("${this.mEntityId}")

		return database.delete(HealthContract.HealthEntry.ENTITY_NAME, whereClause, whereArgs)
	}
}