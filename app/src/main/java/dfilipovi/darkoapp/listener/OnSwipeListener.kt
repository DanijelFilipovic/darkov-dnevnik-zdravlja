package dfilipovi.darkoapp.listener

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class OnSwipeListener(context: Context) : View.OnTouchListener {

	private val gestureDetector = GestureDetector(context, SwipeGestureListener())

	abstract fun onLeftSwipe()
	abstract fun onRightSwipe()

	override fun onTouch(v: View?, event: MotionEvent?): Boolean {
		return gestureDetector.onTouchEvent(event)
	}

	inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {

		private val SWIPE_DISTANCE_THRESHOLD = 100
		private val SWIPE_VELOCITY_THRESHOLD = 100

		override fun onDown(e: MotionEvent?): Boolean {
			return true
		}

		override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
			if (e1 != null && e2 != null) {
				val distanceX: Float = e2.x - e1.x
				if (Math.abs(distanceX) >= SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) >= SWIPE_VELOCITY_THRESHOLD) {
					if (distanceX > 0)
						onRightSwipe()
					else
						onLeftSwipe()
					return true
				}
			}
			return false
		}

	}
}