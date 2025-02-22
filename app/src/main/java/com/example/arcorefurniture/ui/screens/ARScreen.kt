package com.example.arcorefurniture.ui.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arcorefurniture.ui.navigation.MainScreenNav
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
fun ARScreen(
    navController: NavController = rememberNavController(),
    selectedModel: String,
    onShowCategorySheet: () -> Unit
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)


    val modelInstance = remember { mutableListOf<ModelInstance>() }
    val planeRenderer = remember { mutableStateOf(true) }
    val frame = remember { mutableStateOf<Frame?>(null) }

    var startAnchor by remember { mutableStateOf<Anchor?>(null) }
    var endAnchor by remember { mutableStateOf<Anchor?>(null) }
    var distance by remember { mutableStateOf<Float?>(null) }
    var isMeasuring by remember { mutableStateOf(false) }
    var placedNodes by remember { mutableStateOf<List<Node>>(emptyList()) }

    LaunchedEffect(selectedModel) {
        Log.d("ARScreen", "Selected model: $selectedModel")
    }

    Box(modifier = Modifier.fillMaxSize()){

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
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { e: MotionEvent, _: Node? ->
                    val hitTestResult = frame.value?.hitTest(e.x, e.y)
                    val hitPose = hitTestResult?.firstOrNull {
                        it.isValid(depthPoint = false, point = false)
                    }?.hitPose

                    if (hitPose != null) {
                        val newAnchor = hitTestResult.firstOrNull()?.createAnchorOrNull()

                        if (isMeasuring) {
                            if (startAnchor == null) {
                                startAnchor = newAnchor
                                newAnchor?.let {
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
                                endAnchor = newAnchor
                                newAnchor?.let {
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

                                    // Calculate distance when both points are placed
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
                            }
                        } else {
                            modelInstance.clear()

                            newAnchor?.let {
                                val nodeModel = Utils.createAnchorNode(
                                    engine = engine,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstance = modelInstance,
                                    anchor = it,
                                    scale = 1f,
                                    model = selectedModel
                                )
                                childNodes += nodeModel
                            }
                        }
                    }
                }
            )
        )

        // Back Button
        IconButton(
            onClick = { navController.navigate(MainScreenNav) },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Distance display when measuring
        if (isMeasuring && distance != null) {
            Text(
                text = "%.2f meters".format(distance),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
            )
        }

        if (!isMeasuring && selectedModel.isEmpty()) {
            Text(
                text = "Please select a model before placing it.",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom buttons column for better spacing
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row for measure and reset buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Measure Mode Button
                IconButton(
                    onClick = {
                        isMeasuring = !isMeasuring
                        if (!isMeasuring) {
                            startAnchor = null
                            endAnchor = null
                            distance = null
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMeasuring) Color.Blue.copy(alpha = 0.8f) else Color.White.copy(
                                alpha = 0.6f
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Toggle Measure Mode",
                        tint = if (isMeasuring) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Reset Button
                IconButton(
                    onClick = {
                        startAnchor = null
                        endAnchor = null
                        distance = null
                        placedNodes = emptyList()
                        childNodes.clear()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Add Button (matches MainScreen style)
            IconButton(
                onClick = {
                    isMeasuring = false
                    onShowCategorySheet()
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to AR",
                    tint = Color.Black,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}
