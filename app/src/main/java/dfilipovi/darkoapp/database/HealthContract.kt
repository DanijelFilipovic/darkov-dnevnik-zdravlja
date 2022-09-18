package dfilipovi.darkoapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object HealthContract {

	object HealthEntry : BaseColumns {
		const val ENTITY_NAME           = "health"
		const val ATTR_WEIGHT           = "weight"
		const val ATTR_SUGAR            = "sugar"
		const val ATTR_BLOOD_PRESSURE_1 = "blood_pressure_1"
		const val ATTR_BLOOD_PRESSURE_2 = "blood_pressure_2"
		const val ATTR_BLOOD_PRESSURE_3 = "blood_pressure_3"
	}

	private const val SQL_CREATE = "CREATE TABLE ${HealthEntry.ENTITY_NAME} (" +
		"${BaseColumns._ID} INTEGER PRIMARY KEY," +
		"${HealthEntry.ATTR_WEIGHT} REAL," +
		"${HealthEntry.ATTR_SUGAR} INTEGER," +
		"${HealthEntry.ATTR_BLOOD_PRESSURE_1} INTEGER," +
		"${HealthEntry.ATTR_BLOOD_PRESSURE_2} INTEGER," +
		"${HealthEntry.ATTR_BLOOD_PRESSURE_3} INTEGER)"

	private const val SQL_DROP = "DROP TABLE IF EXISTS ${HealthEntry.ENTITY_NAME}"

	class HealthDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

		companion object {
			const val DATABASE_NAME    = "DarkoHealthApp"
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