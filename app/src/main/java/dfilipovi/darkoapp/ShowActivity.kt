package dfilipovi.darkoapp

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.TextView
import dfilipovi.darkoapp.database.WorkContract

class ShowActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_show)
		initialize()
	}

	private fun initialize() {
		val id = this.intent.extras?.get("id") as Int
		if (id != null) {
			queryForEntity(id)?.use {
				it.moveToNext()
				findViewById<TextView>(R.id.show_location).text         = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_LOCATION))
				findViewById<TextView>(R.id.show_date).text             = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_DATE))
				findViewById<TextView>(R.id.show_time_start).text       = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_TIME_START))
				findViewById<TextView>(R.id.show_time_end).text         = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_TIME_END))
				findViewById<TextView>(R.id.show_vehicle).text          = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_VEHICLE))
				findViewById<TextView>(R.id.show_kilometers_start).text = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_KM_START))
				findViewById<TextView>(R.id.show_kilometers_end).text   = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_KM_END))
				findViewById<TextView>(R.id.show_work_type).text        = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_WORK_TYPE))
				findViewById<TextView>(R.id.show_work_order).text       = it.getString(it.getColumnIndex(WorkContract.WorkEntry.ATTR_WORK_ORDER))
			}
		}
	}

	private fun queryForEntity(id: Int): Cursor? {
		val dbHelper = WorkContract.WorkDatabaseHelper(this)
		val database = dbHelper.readableDatabase

		val projection = arrayOf(
			BaseColumns._ID,
			WorkContract.WorkEntry.ATTR_LOCATION,
			WorkContract.WorkEntry.ATTR_DATE,
			WorkContract.WorkEntry.ATTR_TIME_START,
			WorkContract.WorkEntry.ATTR_TIME_END,
			WorkContract.WorkEntry.ATTR_VEHICLE,
			WorkContract.WorkEntry.ATTR_KM_START,
			WorkContract.WorkEntry.ATTR_KM_END,
			WorkContract.WorkEntry.ATTR_WORK_TYPE,
			WorkContract.WorkEntry.ATTR_WORK_ORDER
		)
		val selection = "${BaseColumns._ID} = ?"
		val selectionArgs = arrayOf("${id}")

		val cursor = database.query(WorkContract.WorkEntry.ENTITY_NAME, projection, selection, selectionArgs, null, null, null)
		return cursor
	}

}