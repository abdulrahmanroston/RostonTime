package com.rostontime

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class FloatingService : Service() {
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var prefsManager: PreferencesManager? = null
    private val handler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
        createNotificationChannel()
        startForeground(1, createNotification())
        createFloatingView()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "FLOATING_SERVICE",
                "Roston Time Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "FLOATING_SERVICE")
            .setContentTitle("Roston Time")
            .setContentText("الأيقونة العائمة نشطة")
            .setSmallIcon(R.drawable.ic_time)
            .setOngoing(true)
            .build()
    }

    private fun createFloatingView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget, null)
        
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 100
        layoutParams.y = 100

        windowManager?.addView(floatingView, layoutParams)

        setupFloatingViewListeners(layoutParams)
    }

    private fun setupFloatingViewListeners(layoutParams: WindowManager.LayoutParams) {
        val floatingIcon = floatingView?.findViewById<ImageView>(R.id.floating_icon)
        
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        floatingIcon?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    
                    // Start long press detection
                    if (prefsManager?.isLongPressEnabled() == true) {
                        longPressRunnable = Runnable {
                            showPhrasesDialog()
                        }
                        handler.postDelayed(longPressRunnable!!, prefsManager?.getLongPressDuration()?.toLong() ?: 1000L)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Cancel long press
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    
                    val deltaX = Math.abs(event.rawX - initialTouchX)
                    val deltaY = Math.abs(event.rawY - initialTouchY)
                    
                    if (deltaX < 10 && deltaY < 10) {
                        // It's a click, not a drag
                        insertCurrentTime()
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Cancel long press on move
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(floatingView, layoutParams)
                    true
                }
                else -> false
            }
        }
    }

    private fun insertCurrentTime() {
        val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        
        val intent = Intent(this, TextInputService::class.java)
        intent.putExtra("text_to_insert", currentTime)
        startService(intent)
    }

    private fun showPhrasesDialog() {
        val phrases = prefsManager?.getCustomPhrases() ?: emptyList()
        if (phrases.isNotEmpty()) {
            val intent = Intent(this, PhraseSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager?.removeView(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
