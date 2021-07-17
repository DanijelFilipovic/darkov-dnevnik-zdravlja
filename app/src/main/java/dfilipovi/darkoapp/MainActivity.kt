package dfilipovi.darkoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import dfilipovi.darkoapp.database.WorkContract
import dfilipovi.darkoapp.fragment.MonthFragment
import dfilipovi.darkoapp.listener.OnSwipeListener
import java.util.*

class MainActivity : AppCompatActivity() {

	private val mCalendar: Calendar = Calendar.getInstance()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initializeMonthNameAndYear()
		initializeMonthFragment(savedInstanceState)
		initializeEvents()
	}

	override fun onDestroy() {
		super.onDestroy()
		WorkContract.WorkDatabaseHelper(this).close()
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
		val monthName: String = when (mCalendar[Calendar.MONTH]) {
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
		val year: Int = mCalendar[Calendar.YEAR]
		val calendarMonthAndYear: TextView = findViewById(R.id.calendar_month_and_year)
		calendarMonthAndYear.text = "%s, %d".format(monthName, year)
	}

	private fun initializeMonthFragment(savedInstanceState: Bundle?) {
		val monthFragment = MonthFragment.newInstance(mCalendar.clone() as Calendar)
		if (savedInstanceState == null) {
			supportFragmentManager.commit {
				add(R.id.fragment_month, monthFragment)
			}
		}
	}

	private fun initializeEvents() {
		val main: View = findViewById(R.id.activity_main)
		main.setOnTouchListener(object: OnSwipeListener(this) {
			override fun onLeftSwipe() {
				mCalendar.set(Calendar.MONTH, mCalendar[Calendar.MONTH] + 1)
				initializeMonthNameAndYear()
				supportFragmentManager.commit {
					replace(R.id.fragment_month, MonthFragment.newInstance(mCalendar.clone() as Calendar))
				}
			}
			override fun onRightSwipe() {
				mCalendar.set(Calendar.MONTH, mCalendar[Calendar.MONTH] - 1)
				initializeMonthNameAndYear()
				supportFragmentManager.commit {
					replace(R.id.fragment_month, MonthFragment.newInstance(mCalendar.clone() as Calendar))
				}
			}

		})
	}
}