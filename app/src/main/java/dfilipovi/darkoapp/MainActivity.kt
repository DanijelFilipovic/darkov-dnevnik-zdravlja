package dfilipovi.darkoapp

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.text.TextUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import dfilipovi.darkoapp.database.WorkContract
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

	private val bufferDaysInMonth: HashMap<String, LinearLayout> = HashMap()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initializeMonthNameAndYear()
		initializeDaysInMonth()
	}

	override fun onDestroy() {
		super.onDestroy()
		WorkContract.WorkDatabaseHelper(this).close()
	}

	override fun onResume() {
		super.onResume()
		clearCalendar()
		queryForCalendar().use {
			while(it.moveToNext()) {
				insertIntoCalendar(it)
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = this.menuInflater
		inflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.task_new -> {
				val intent = Intent(this, AddActivity::class.java)
				startActivity(intent)
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun initializeMonthNameAndYear() {
		val calendar: Calendar = Calendar.getInstance();
		val monthName: String = when (calendar[Calendar.MONTH]) {
			0 -> "siječanj"
			1 -> "veljača"
			2 -> "ožujak"
			3 -> "travanj"
			4 -> "svibanj"
			5 -> "lipanj"
			6 -> "srpanj"
			7 -> "kolovoz"
			8 -> "rujan"
			9 -> "listopad"
			10 -> "studeni"
			else -> "prosinac"
		}
		val year: Int = calendar[Calendar.YEAR]
		val calendarMonthAndYear: TextView = findViewById(R.id.calendar_month_and_year)
		calendarMonthAndYear.text = "%s, %d".format(monthName, year)
	}

	private fun initializeDaysInMonth() {
		val calendar: Calendar = Calendar.getInstance();
		var firstDay: Int = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
		var lastDay: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

		calendar.set(Calendar.DAY_OF_MONTH, 1)
		val offset: Int = when (calendar[Calendar.DAY_OF_WEEK]) {
			Calendar.MONDAY -> 0
			Calendar.TUESDAY -> 1
			Calendar.WEDNESDAY -> 2
			Calendar.THURSDAY -> 3
			Calendar.FRIDAY -> 4
			Calendar.SATURDAY -> 5
			else -> 6
		}

		firstDay += offset
		lastDay += offset
		for (i in firstDay..lastDay) {
			val id: Int = resources.getIdentifier("calendar_day_%d".format(i), "id", packageName)
			val layout: LinearLayout = findViewById(id)

			val date: String = "%02d.%02d.%04d.".format(i - offset, calendar[Calendar.MONTH] + 1, calendar[Calendar.YEAR])
			bufferDaysInMonth[date] = layout;

			val tvDayOfTheMonth: TextView = TextView(this)
			tvDayOfTheMonth.text = (i - offset).toString()
			tvDayOfTheMonth.textAlignment = TextView.TEXT_ALIGNMENT_CENTER;
			layout.addView(tvDayOfTheMonth)
		}
	}

	private fun clearCalendar() {
		for (key in bufferDaysInMonth.keys) {
			val layout = bufferDaysInMonth[key]
			if (layout != null) {
				while (layout.childCount > 1) {
					layout.removeViewAt(1)
				}
			}
		}
	}

	private fun queryForCalendar(): Cursor {
		val calendar = Calendar.getInstance()
		val startOfTheMonth = "%d-%02d-01".format(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1)
		val endOfTheMonth = "%d-%02d-%02d".format(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

		val dbHelper = WorkContract.WorkDatabaseHelper(this)
		val database = dbHelper.readableDatabase
		val projection = arrayOf(
			BaseColumns._ID,
			WorkContract.WorkEntry.ATTR_LOCATION,
			WorkContract.WorkEntry.ATTR_DATE
		)
		val selection = "(SUBSTR(date, 7, 4) || '-' || SUBSTR(date, 4, 2) || '-' || SUBSTR(date, 1, 2)) BETWEEN ? AND ?"
		val selectionArgs = arrayOf(startOfTheMonth, endOfTheMonth)

		return database.query(WorkContract.WorkEntry.ENTITY_NAME, projection, selection, selectionArgs, null, null, null)
	}

	private fun insertIntoCalendar(cursor: Cursor) {
		val date = cursor.getString(cursor.getColumnIndex(WorkContract.WorkEntry.ATTR_DATE))
		val layout = bufferDaysInMonth[date]
		if (layout != null && layout.childCount <= 4) {
			val tvEntity = TextView(this)
			tvEntity.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
			tvEntity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
			tvEntity.maxLines = 1
			tvEntity.ellipsize = TextUtils.TruncateAt.END
			tvEntity.text = if (layout.childCount < 4) cursor.getString(cursor.getColumnIndex(WorkContract.WorkEntry.ATTR_LOCATION)) else "..."
			layout.addView(tvEntity)

			layout.setOnClickListener {
				val dbHelper = WorkContract.WorkDatabaseHelper(this)
				val database = dbHelper.readableDatabase
				val projection = arrayOf(BaseColumns._ID, WorkContract.WorkEntry.ATTR_LOCATION)
				val selection = "date = ?"
				val selectionArgs = arrayOf(date)

				val cursor = database.query(WorkContract.WorkEntry.ENTITY_NAME, projection, selection, selectionArgs, null, null, null)
				if (cursor.count > 1) {
					val ids = ArrayList<Int>()
					val locations = ArrayList<String>()

					cursor.use {
						while (it.moveToNext()) {
							ids.add(it.getInt(it.getColumnIndex(BaseColumns._ID)))
							locations.add(it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_LOCATION)))
						}
					}

					AlertDialog.Builder(this)
						.setTitle(getString(R.string.choose_location))
						.setItems(locations.toTypedArray()) { _, which ->
							val intent = Intent(this, ShowActivity::class.java)
							intent.putExtra("id", ids[which])
							startActivity(intent)
						}
						.setNegativeButton(getString(R.string.cancel), null)
						.create()
						.show()
				} else {
					var id: Int? = null
					cursor.use {
						it.moveToNext()
						id = it.getInt(it.getColumnIndex(BaseColumns._ID))
					}
					val intent = Intent(this, ShowActivity::class.java)
					intent.putExtra("id", id)
					startActivity(intent)
				}


			}
		}
	}
}