package com.rostontime

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.os.Bundle

class TextInputService : AccessibilityService() {
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val textToInsert = intent?.getStringExtra("text_to_insert")
        if (textToInsert != null) {
            insertText(textToInsert)
        }
        return START_NOT_STICKY
    }

    private fun insertText(text: String) {
        val arguments = Bundle()
        arguments.putCharSequence(
            android.view.accessibility.AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        
        rootInActiveWindow?.let { rootNode ->
            val focusedNode = rootNode.findFocus(
                android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT
            )
            
            focusedNode?.performAction(
                android.view.accessibility.AccessibilityNodeInfo.ACTION_SET_TEXT,
                arguments
            ) ?: run {
                // Fallback: try to paste using global action
                performGlobalAction(GLOBAL_ACTION_PASTE)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed for our use case
    }

    override fun onInterrupt() {
        // Not needed for our use case
    }
}
