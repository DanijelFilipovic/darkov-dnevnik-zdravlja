package dfilipovi.darkoapp

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import dfilipovi.darkoapp.database.WorkContract

class ShowActivity : AppCompatActivity() {

	private var mEntityId: Int = -1;

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_show)
		initialize()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = this.menuInflater
		inflater.inflate(R.menu.show_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.task_update -> {
				return true
			}
			R.id.task_delete -> {
				val numberOfRowsAffected = deleteEntity()
				if (numberOfRowsAffected > 0) {
					Toast.makeText(this, "Izbrisano", Toast.LENGTH_SHORT).show()
					finish()
				}
				else
					Toast.makeText(this, "Došlo je do greške. Brisanje nije provedeno.", Toast.LENGTH_SHORT).show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun initialize() {
		this.mEntityId = this.intent.extras?.get("id") as Int
		if (this.mEntityId != -1) {
			queryForEntity(this.mEntityId)?.use {
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

	private fun deleteEntity(): Int {
		val dbHelper = WorkContract.WorkDatabaseHelper(this)
		val database = dbHelper.writableDatabase
		val whereClause = "${BaseColumns._ID} = ?"
		val whereArgs = arrayOf("${this.mEntityId}")
		return database.delete(WorkContract.WorkEntry.ENTITY_NAME, whereClause, whereArgs)
	}
}