/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.cse118_lab4.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.cse118_lab4.presentation.theme.CSE118Lab4Theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max

import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null

    private var heartRateApp by mutableStateOf("Waiting for heart rate...")
    private var heartRate by mutableIntStateOf(0)

    private val serverIP = "192.168.0.225"
    private val serverPort = 7777

    private var clientSocket: Socket? = null
    private var outputStream: OutputStream? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("HeartRate", "Did not have access.")
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BODY_SENSORS), 1
            )
        }
        else{
            Log.d("HeartRate", "Had access.")
        }

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        if (heartRateSensor == null) {
            Log.d("HeartRate", "Heart rate sensor not available.")
        } else {
            Log.d("HeartRate", "Heart rate sensor available.")
        }


        val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val arrayList = ArrayList<String>()

        for (sensor in sensors) {
            arrayList.add(sensor.name)
        }

        arrayList.forEach { n: String? -> Log.d("SensorNames", n ?: "null") }



//        setContentView(R.layout.activity_main)
        connectToServer()
    }

    private fun connectToServer() {
        thread {
            try {
                clientSocket = Socket(serverIP, serverPort)
                outputStream = clientSocket?.getOutputStream()
                Log.d("TCPClient", "Connected to server.")
            } catch (e: Exception) {
                Log.e("TCPClient", "Error connecting to server: ${e.message}")
            }
        }
    }

    private fun sendHeartRate(heartRate: Int) {
        thread {
            try {
                outputStream?.write("$heartRate".toByteArray())
                outputStream?.flush()
                Log.d("TCPClient", "Sent heart rate: $heartRate")
            } catch (e: Exception) {
                Log.e("TCPClient", "Error sending data: ${e.message}")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        heartRateSensor?.also {
            Log.d("HeartRate", "Resumed")
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            heartRate = event.values[0].toInt()
            heartRateApp = "Heart Rate: $heartRate BPM"
            Log.d("HeartRate", "Instantaneous heart rate: $heartRate")
            sendHeartRate(heartRate)
        }
        else{
            Log.d("HeartRate", "No heart rate sensor available.")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes here if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close socket when the app is destroyed
        clientSocket?.close()
    }


    @Composable
    fun WearApp() {
        CSE118Lab4Theme {
            val beatDurationMillis = 40000 / max(heartRate, 1) // Use animateFloatAsState to smoothly transition the scale value
            val scale by animateFloatAsState(
                targetValue = if (heartRate > 0) 1.3f else 1f, // Scale value based on heart rate
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (beatDurationMillis / 2), // Half duration for expanding
                        Log.d("HeartSpeed", "Duration: $beatDurationMillis"),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = heartRateApp,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
//                    TimeText()

                    // Animated Heart
                    Canvas(
                        modifier = Modifier.size(150.dp)
                    ) {
                        val heartPath = createHeartPath(size.minDimension / 5)
                        withTransform({
                            val offsetX = -size.width / 8 // Move left by 1/8th of the canvas width
                            val offsetY = -size.height / 4 // Move up by 1/8th of the canvas height

                            translate(left = size.width / 2 + offsetX, top = size.height / 2 + offsetY)

//                            translate(left = size.width / 2, top = size.height / 2)
                            // Apply scaling relative to the center
                            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
                        }) {
                            drawPath(
                                path = heartPath,
                                color = Color.Red,
                                style = androidx.compose.ui.graphics.drawscope.Fill
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createHeartPath(radius: Float): Path {
        val scaleFactor = radius / 50f
        return Path().apply {
            moveTo(50f * scaleFactor, 20f * scaleFactor)
            cubicTo(
                35f * scaleFactor, 0f * scaleFactor,
                0f * scaleFactor, 25f * scaleFactor,
                50f * scaleFactor, 70f * scaleFactor
            )
            cubicTo(
                100f * scaleFactor, 25f * scaleFactor,
                65f * scaleFactor, 0f * scaleFactor,
                50f * scaleFactor, 20f * scaleFactor
            )
            close()
        }
    }

    @Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        WearApp()
    }
}