package dfilipovi.darkoapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import dfilipovi.darkoapp.database.WorkContract
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AddActivity : AppCompatActivity() {

	private val dateFormatter: DateFormat = SimpleDateFormat("dd.MM.yyyy.")

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add)

		initializeDatePicker()
		initializeTimePickers()
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
		val location: String = findViewById<EditText>(R.id.input_location).text.toString()
		val date: String = findViewById<EditText>(R.id.input_date).text.toString()
		val timeStart: String = findViewById<EditText>(R.id.input_time_start).text.toString()
		val timeEnd: String = findViewById<EditText>(R.id.input_time_end).text.toString()
		val vehicle: String = findViewById<EditText>(R.id.input_vehicle).text.toString()
		val kilometersStart: String = findViewById<EditText>(R.id.input_kilometers_start).text.toString()
		val kilometersEnd: String = findViewById<EditText>(R.id.input_kilometers_end).text.toString()
		val workType: String = findViewById<EditText>(R.id.input_work_type).text.toString()
		val workOrder: String = findViewById<EditText>(R.id.input_work_order).text.toString()

		val missing: ArrayList<String> = checkForMissing(location, timeStart, timeEnd, vehicle, kilometersStart, kilometersEnd, workType, workOrder)

		if (missing.isNotEmpty()) {
			showErrorDialog(missing)
		} else {
			val dbHelper = WorkContract.WorkDatabaseHelper(this)
			val db = dbHelper.writableDatabase

			val values = ContentValues().apply {
				put(WorkContract.WorkEntry.ATTR_LOCATION, location)
				put(WorkContract.WorkEntry.ATTR_DATE, date)
				put(WorkContract.WorkEntry.ATTR_TIME_START, timeStart)
				put(WorkContract.WorkEntry.ATTR_TIME_END, timeEnd)
				put(WorkContract.WorkEntry.ATTR_VEHICLE, vehicle)
				put(WorkContract.WorkEntry.ATTR_KM_START, kilometersStart)
				put(WorkContract.WorkEntry.ATTR_KM_END, kilometersEnd)
				put(WorkContract.WorkEntry.ATTR_WORK_TYPE, workType)
				put(WorkContract.WorkEntry.ATTR_WORK_ORDER, workOrder)
			}

			val id: Long? = db?.insert(WorkContract.WorkEntry.ENTITY_NAME, null, values)
			if (id != null && !id.equals(-1)) {
				Toast.makeText(this, "Uspješno spremljeno.", Toast.LENGTH_SHORT).show()
				finish()
			} else {
				Toast.makeText(this, "Greška u spremanju.", Toast.LENGTH_SHORT).show()
			}
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

	private fun initializeTimePickers() {
		val txtTimeStart: EditText = findViewById(R.id.input_time_start)
		val txtTimeEnd: EditText = findViewById(R.id.input_time_end)
		initializeTimePickerDialog(txtTimeStart)
		initializeTimePickerDialog(txtTimeEnd)
	}

	private fun initializeTimePickerDialog(editText: EditText) {
		editText.setOnClickListener {
			val text: String = editText.text.toString();
			var initialHourOfDay: Int = 12;
			var initialMinute: Int = 0;
			if (!text.isEmpty()) {
				initialHourOfDay = text.substringBefore(":", "12").trim().toInt()
				initialMinute = text.substringAfter(":", "0").trim().toInt()
			}

			TimePickerDialog(
				this,
				{ _, hourOfDay, minute -> editText.setText("%02d:%02d".format(hourOfDay, minute), TextView.BufferType.EDITABLE)  },
				initialHourOfDay,
				initialMinute,
				true
			).show()
		}
	}

	private fun checkForMissing(
		location: String,
		timeStart: String,
		timeEnd: String,
		vehicle: String,
		kilometersStart: String,
		kilometersEnd: String,
		workType: String,
		workOrder: String
	): ArrayList<String> {
		val missing: ArrayList<String> = ArrayList()
		if (location.isBlank())
			missing.add(getString(R.string.label_location))
		if (timeStart.isBlank())
			missing.add(getString(R.string.label_time) + " " + getString(R.string.label_time_start).lowercase())
		if (timeEnd.isBlank())
			missing.add(getString(R.string.label_time) + " " + getString(R.string.label_time_end).lowercase())
		if (vehicle.isBlank())
			missing.add(getString(R.string.label_vehicle))
		if (kilometersStart.isBlank())
			missing.add(getString(R.string.label_kilometers_start) + " " + getString(R.string.label_kilometers).lowercase())
		if (kilometersEnd.isBlank())
			missing.add(getString(R.string.label_kilometers_end) + " " + getString(R.string.label_kilometers).lowercase())
		if (workType.isBlank())
			missing.add(getString(R.string.label_work_type))
		if (workOrder.isBlank())
			missing.add(getString(R.string.label_work_order))
		return missing
	}

	private fun showErrorDialog(missing: ArrayList<String>) {
		val messageView: LinearLayout = LinearLayout(this)
		messageView.orientation = LinearLayout.VERTICAL
		messageView.setPadding(73, 24, 73, 24)

		val tvMessage = TextView(this)
		tvMessage.text = getString(R.string.error_incomplete_fields_message)
		tvMessage.setTextColor(getColor(R.color.black))
		tvMessage.setPadding(0, 0, 0, 5)
		messageView.addView(tvMessage)

		for (item in missing) {
			val tvItem = TextView(this)
			tvItem.text = item;
			tvItem.setTextColor(getColor(R.color.black))
			tvItem.setPadding(30, 5, 0, 5)
			messageView.addView(tvItem)
		}

		AlertDialog.Builder(this)
			.setTitle(getString(R.string.error_incomplete_fields_title))
			.setView(messageView)
			.setPositiveButton("OK", null)
			.create()
			.show()
	}


}