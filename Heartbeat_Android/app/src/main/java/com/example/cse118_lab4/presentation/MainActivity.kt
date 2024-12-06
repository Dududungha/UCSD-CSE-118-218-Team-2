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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.wear.compose.material.CircularProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.concurrent.thread


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null

    private var heartRateApp by mutableStateOf("Waiting for heart rate...")
    private var heartRate by mutableIntStateOf(0)

    private val serverIP = "192.168.0.203" // For 3215_5G
//    private val serverIP = "192.168.1.227"
    private val serverPort = 7777

    private var clientSocket: Socket? = null
    private var outputStream: OutputStream? = null

    private val minHeartRate = 30f
    private val maxHeartRate = 125f


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

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
                val buffer = ByteBuffer.allocate(4)
                buffer.putInt(heartRate)
//                outputStream?.write("$heartRate".toByteArray())
                Log.d("TCPClient", "Byte: ${buffer.array().contentToString()}")
                outputStream?.write(buffer.array())
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
            val beatDurationMillis = 60000 / max(heartRate, 1) // Use animateFloatAsState to smoothly transition the scale value
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

            val waveDurationMillis = (60000L / max(heartRate, 1)) // Convert BPM to milliseconds per cycle

            var phase by remember { mutableStateOf(0f) }

            LaunchedEffect(heartRate) {
                launch {
                    while (true) {
                        phase = (phase + 0.01f) % 1f // Loop the phase from 0 to 1
                        delay(waveDurationMillis / 100) // Control the speed of the animation based on heart rate
                    }
                }
            }

            var heartRateColor by remember { mutableStateOf(Color.Gray) }
            val heartRateColorCalculated = heartRateToColor(heartRate)
            LaunchedEffect(heartRate) {
                heartRateColor = heartRateColorCalculated
            }

            val progress = ((heartRate - minHeartRate).toFloat() / (maxHeartRate - minHeartRate)) .coerceIn(0.0f, 1.0f)

            //circle progress indicator
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .graphicsLayer(rotationZ = 180f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize().padding(all = 1.dp)
                        .clearAndSetSemantics {},
                    startAngle = 340.5f,
                    endAngle = 200.5f,
                    indicatorColor = heartRateColor,
                    progress = progress,
                    strokeWidth = 8.dp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(top = 50.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = heartRateApp,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    //                    TimeText()

                    
                    // Animated Heart and ECG Wave
                    Canvas(
                        modifier = Modifier.size(200.dp)
                    ) {
                        val heartPath = createHeartPath(size.minDimension / 5)
                        withTransform({
                            val offsetX =
                                -size.width / 8 // Move left by 1/8th of the canvas width
                            val offsetY =
                                -size.height / 11 * 5 // Move up by 1/8th of the canvas height

                            translate(
                                left = size.width / 2 + offsetX,
                                top = size.height / 2 + offsetY
                            )

                            // Apply scaling relative to the center
                            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
                        }) {
                            drawPath(
                                path = heartPath,
                                color = heartRateColor,
                                style = androidx.compose.ui.graphics.drawscope.Fill
                            )
                        }


                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw ECG Wave
                        val waveHeight = canvasHeight * 0.4f // Wave occupies 40% of height
                        val amplitude = waveHeight / 4
                        val waveStartY = canvasHeight * 0.7f // Start wave at 70% height

                        val wavePath = Path().apply {
                            moveTo(0f, waveStartY)
                            val waveLength = canvasWidth / 4
                            for (x in 0 until canvasWidth.toInt() step 10) {
                                val y =
                                    waveStartY - amplitude * kotlin.math.sin((x + phase * waveLength).toDouble() * (2 * Math.PI / waveLength))
                                        .toFloat()
                                lineTo(x.toFloat(), y)
                            }
                        }
                        drawPath(
                            path = wavePath,
                            color = heartRateColor,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                    }
                }
            }
        }
    }

    private fun heartRateToColor(heartRate: Int): Color {

        if(heartRate < 50){
            return Color.Gray
        }

        val fraction = ((heartRate - minHeartRate).toFloat() / (maxHeartRate - minHeartRate)).coerceIn(0.0f, 1.0f)
//        val fraction = 0.65f
        val color1 = lerp(Color.Blue, Color.Red, fraction)

        Log.d("HeartRateColor", "Generated Color (RGB): Hue: $fraction")
        return color1
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
