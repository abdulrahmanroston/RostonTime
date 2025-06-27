package com.rostontime

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rostontime.ui.theme.RostonTimeTheme

class PhraseSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            RostonTimeTheme {
                PhraseSelectionScreen(
                    onPhraseSelected = { phrase ->
                        val intent = Intent(this, TextInputService::class.java)
                        intent.putExtra("text_to_insert", phrase)
                        startService(intent)
                        finish()
                    },
                    onDismiss = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseSelectionScreen(
    onPhraseSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val phrases = remember { prefsManager.getCustomPhrases() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر الجملة") },
        text = {
            LazyColumn {
                items(phrases) { phrase ->
                    TextButton(
                        onClick = { onPhraseSelected(phrase) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(phrase)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
