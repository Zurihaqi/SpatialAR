package com.example.arcorefurniture.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arcorefurniture.ui.utils.Utils
import com.google.ar.core.*
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.distance
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

@Composable
fun ARScreen(navController: NavController = rememberNavController(), models: List<String>) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)

    val modelInstance = remember {
        mutableListOf<ModelInstance>()
    }
    val planeRenderer = remember { mutableStateOf(true) }
    val frame = remember { mutableStateOf<Frame?>(null) }

    var startAnchor by remember { mutableStateOf<Anchor?>(null) }
    var endAnchor by remember { mutableStateOf<Anchor?>(null) }
    var distance by remember { mutableStateOf<Float?>(null) }

    var isMeasuring by remember { mutableStateOf(true) } // Menentukan mode aktif
    var placedNodes by remember { mutableStateOf<List<Node>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.weight(2f)) {
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
                            val newAnchor = hitTestResult.firstOrNull()?.createAnchorOrNull()

                            if (isMeasuring) {
                                // Mode pengukuran
                                if (startAnchor == null && isMeasuring) {
                                    startAnchor = newAnchor
                                    newAnchor?.let{
                                        val nodeModel = Utils.createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstance = modelInstance,
                                            anchor = it,
                                            scale = 0.2f,
                                            model = "models/props/map_pin_location_pin.glb"
                                        )
                                        childNodes += nodeModel
                                    }
                                } else if (endAnchor == null) {
                                    newAnchor?.let{
                                        val nodeModel = Utils.createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstance = modelInstance,
                                            anchor = it,
                                            scale = 0.2f,
                                            model = "models/props/map_pin_location_pin.glb"
                                        )
                                        childNodes += nodeModel
                                    }
                                    endAnchor = newAnchor
                                    newAnchor?.let{
                                        val nodeModel = Utils.createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstance = modelInstance,
                                            anchor = it,
                                            scale = 0.2f,
                                            model = "models/props/map_pin_location_pin.glb"
                                        )
                                        childNodes += nodeModel
                                    }
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
                                    // Reset jika sudah ada dua titik
                                    startAnchor = newAnchor
                                    endAnchor = null
                                    distance = null
                                }
                            } else {
                                // Mode penempatan objek
                                modelInstance.clear()
                                newAnchor?.let {
                                    val nodeModel = Utils.createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        modelInstance = modelInstance,
                                        anchor = it,
                                        scale = 1f,
                                        model = "models/sofas/sofa_03_1k.gltf/sofa_03_1k.gltf"
                                    )
                                    childNodes += nodeModel
                                }
                            }
                        }
                    }
                )
            )
        }

        // UI untuk memilih mode dan menampilkan hasil pengukuran
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isMeasuring) "Mode: Measurement" else "Mode: Place Object",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { isMeasuring = true }) {
                    Text("Measurement")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { isMeasuring = false }) {
                    Text("Place Object")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isMeasuring) {
                Text(
                    text = distance?.let { "Distance: %.2f meters".format(it) } ?: "Tap two points to measure",
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    startAnchor = null
                    endAnchor = null
                    distance = null
                    placedNodes.forEach { it}
                    placedNodes = emptyList()
                    childNodes.clear()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Reset")
            }
        }
    }
}
