package com.rostontime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostontime.ui.theme.RostonTimeTheme

class MainActivity : ComponentActivity() {
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    private val accessibilityPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            RostonTimeTheme {
                MainScreen(
                    onRequestOverlayPermission = { requestOverlayPermission() },
                    onRequestAccessibilityPermission = { requestAccessibilityPermission() },
                    onStartService = { startFloatingService() },
                    onStopService = { stopFloatingService() }
                )
            }
        }
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun requestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        accessibilityPermissionLauncher.launch(intent)
    }

    private fun startFloatingService() {
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(this, FloatingService::class.java)
            startForegroundService(intent)
        }
    }

    private fun stopFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        stopService(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRequestOverlayPermission: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    
    var isServiceRunning by remember { mutableStateOf(false) }
    var customPhrases by remember { mutableStateOf(prefsManager.getCustomPhrases()) }
    var longPressEnabled by remember { mutableStateOf(prefsManager.isLongPressEnabled()) }
    var longPressDuration by remember { mutableStateOf(prefsManager.getLongPressDuration()) }
    var showAddPhraseDialog by remember { mutableStateOf(false) }
    var newPhrase by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Roston Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "التحكم في الخدمة",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onRequestOverlayPermission()
                                    onRequestAccessibilityPermission()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("طلب الصلاحيات")
                            }
                            
                            if (isServiceRunning) {
                                Button(
                                    onClick = {
                                        onStopService()
                                        isServiceRunning = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إيقاف")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        onStartService()
                                        isServiceRunning = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تشغيل")
                                }
                            }
                        }
                    }
                }
            }

            // Long Press Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "إعدادات الضغط المطول",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تفعيل الضغط المطول")
                            Switch(
                                checked = longPressEnabled,
                                onCheckedChange = { 
                                    longPressEnabled = it
                                    prefsManager.setLongPressEnabled(it)
                                }
                            )
                        }
                        
                        if (longPressEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("مدة الضغط (ثانية): ${longPressDuration / 1000f}")
                            Slider(
                                value = longPressDuration.toFloat(),
                                onValueChange = { 
                                    longPressDuration = it.toInt()
                                    prefsManager.setLongPressDuration(it.toInt())
                                },
                                valueRange = 500f..3000f,
                                steps = 5
                            )
                        }
                    }
                }
            }

            // Custom Phrases Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "الجمل المخصصة",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showAddPhraseDialog = true }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "إضافة جملة")
                            }
                        }
                    }
                }
            }

            // Custom Phrases List
            itemsIndexed(customPhrases) { index, phrase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            phrase,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = {
                                val newList = customPhrases.toMutableList()
                                newList.removeAt(index)
                                customPhrases = newList
                                prefsManager.setCustomPhrases(newList)
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Phrase Dialog
    if (showAddPhraseDialog) {
        AlertDialog(
            onDismissRequest = { showAddPhraseDialog = false },
            title = { Text("إضافة جملة جديدة") },
            text = {
                OutlinedTextField(
                    value = newPhrase,
                    onValueChange = { newPhrase = it },
                    label = { Text("الجملة") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPhrase.isNotBlank()) {
                            val newList = customPhrases.toMutableList()
                            newList.add(newPhrase)
                            customPhrases = newList
                            prefsManager.setCustomPhrases(newList)
                            newPhrase = ""
                            showAddPhraseDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddPhraseDialog = false
                        newPhrase = ""
                    }
                ) {
                    Text("إلغاء")
                }
            }
        )
    }
}
