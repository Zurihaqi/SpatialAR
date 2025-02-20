package com.example.arcorefurniture.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.ar.core.*
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.distance
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

@Composable
@Preview
fun ARScreen(navController: NavController = rememberNavController()) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)

    val planeRenderer = remember { mutableStateOf(true) }
    val frame = remember { mutableStateOf<Frame?>(null) }

    var startAnchor by remember { mutableStateOf<Anchor?>(null) }
    var endAnchor by remember { mutableStateOf<Anchor?>(null) }
    var distance by remember { mutableStateOf<Float?>(null) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.weight(1f)) {
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                planeRenderer = planeRenderer.value,
                cameraNode = cameraNode,
                materialLoader = materialLoader,
                onSessionUpdated = { _, updatedFrame -> frame.value = updatedFrame },
                sessionConfiguration = { session, config ->
                    config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { e: MotionEvent, node: Node? ->
                        val hitTestResult = frame.value?.hitTest(e.x, e.y)
                        val hitPose = hitTestResult?.firstOrNull {
                            it.isValid(depthPoint = false, point = false)
                        }?.hitPose

                        if (hitPose != null) {
                            val newAnchor = hitTestResult.firstOrNull()?.createAnchor()
                            if (startAnchor == null) {
                                startAnchor = newAnchor
                            } else if (endAnchor == null) {
                                endAnchor = newAnchor

                                // Hitung jarak antara dua titik
                                if (startAnchor != null && endAnchor != null) {
                                    val startPos = Float3(
                                        startAnchor!!.pose.tx(),
                                        startAnchor!!.pose.ty(),
                                        startAnchor!!.pose.tz()
                                    )
                                    val endPos = Float3(
                                        endAnchor!!.pose.tx(),
                                        endAnchor!!.pose.ty(),
                                        endAnchor!!.pose.tz()
                                    )

                                    distance = distance(startPos, endPos)
                                }
                            } else {
                                // Reset jika sudah dua titik
                                startAnchor = newAnchor
                                endAnchor = null
                                distance = null
                            }
                        }
                    }
                )
            )
        }

        // Menampilkan hasil pengukuran jarak
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = distance?.let { "Distance: %.2f meters".format(it) } ?: "Tap two points to measure",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        startAnchor = null
                        endAnchor = null
                        distance = null
                    },
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
