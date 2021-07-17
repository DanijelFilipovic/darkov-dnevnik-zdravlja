package dfilipovi.darkoapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object WorkContract {

	object WorkEntry : BaseColumns {
		const val ENTITY_NAME     = "work"
		const val ATTR_LOCATION   = "location"
		const val ATTR_DATE       = "date"
		const val ATTR_TIME_START = "time_start"
		const val ATTR_TIME_END   = "time_end"
		const val ATTR_VEHICLE    = "vehicle"
		const val ATTR_KM_START   = "km_start"
		const val ATTR_KM_END     = "km_end"
		const val ATTR_WORK_TYPE  = "work_type"
		const val ATTR_WORK_ORDER = "work_order"
	}

	private const val SQL_CREATE = "CREATE TABLE ${WorkEntry.ENTITY_NAME} (" +
		"${BaseColumns._ID} INTEGER PRIMARY KEY," +
		"${WorkEntry.ATTR_LOCATION} TEXT," +
		"${WorkEntry.ATTR_DATE} TEXT," +
		"${WorkEntry.ATTR_TIME_START} TEXT," +
		"${WorkEntry.ATTR_TIME_END} TEXT," +
		"${WorkEntry.ATTR_VEHICLE} TEXT," +
		"${WorkEntry.ATTR_KM_START} INTEGER," +
		"${WorkEntry.ATTR_KM_END} INTEGER," +
		"${WorkEntry.ATTR_WORK_TYPE} TEXT," +
		"${WorkEntry.ATTR_WORK_ORDER} TEXT)"

	private const val SQL_DROP = "DROP TABLE IF EXISTS ${WorkEntry.ENTITY_NAME}"

	class WorkDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

		companion object {
			const val DATABASE_NAME    = "DarkoApp"
			const val DATABASE_VERSION = 1
		}

		override fun onCreate(db: SQLiteDatabase?) {
			db?.execSQL(SQL_CREATE);
		}

		override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
			if (db != null) {
				db.execSQL(SQL_DROP)
				onCreate(db);
			}
		}

	}

}