package com.sid.graphicstest3

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sqrt

class MainActivity : Activity() {
	private lateinit var graphicsView: GraphicsView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		graphicsView = GraphicsView(this)
		graphicsView.setEGLContextClientVersion(3)
		graphicsView.setRenderer(Renderer())
		setContentView(graphicsView)
	}

	override fun onPause() {
		super.onPause()
		graphicsView.onPause()
	}

	override fun onResume() {
		super.onResume()
		graphicsView.onResume()
	}

	companion object {
		val camera = Camera()
		val scene = Scene()
	}
}

class GraphicsView(context: Context) : GLSurfaceView(context) {
	private var previousX = 0.0f
	private var previousY = 0.0f

	init {
		setOnTouchListener { v, event ->
			when (event.pointerCount) {
				4 -> when (event.action) {
					MotionEvent.ACTION_MOVE -> MainActivity.camera.reset()
				}

				3 -> when (event.action) {
					MotionEvent.ACTION_MOVE -> {
						val deltaX = event.getX(0) - event.getX(1)
						val deltaY = event.getY(0) - event.getY(1)
						val distance =
							sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
						val deltaDistance = distance - previousX

						MainActivity.camera.zoom(deltaDistance)
						MainActivity.camera.rotate(deltaX, deltaY)
						previousX = distance
					}
				}

				2 -> when (event.action) {
					MotionEvent.ACTION_MOVE -> {
						val deltaX = event.getX(0) - event.getX(1)
						val deltaY = event.getY(0) - event.getY(1)
						val distance =
							sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
						val deltaDistance = distance - previousX

						MainActivity.camera.zoom(deltaDistance)
						previousX = distance
					}
				}

				1 -> when (event.action) {
					MotionEvent.ACTION_DOWN -> {
						previousX = event.x
						previousY = event.y
					}

					MotionEvent.ACTION_MOVE -> {
						val deltaX = event.x - previousX
						val deltaY = event.y - previousY

						MainActivity.camera.pan(-deltaX, deltaY)
						previousX = event.x
						previousY = event.y
					}
				}
			}

			v.performClick()
			true
		}
	}
}

class Scene {
	private val shapes = mutableListOf<Shape>()

	init {
		shapes.add(Cube(0.5f))
		moveLast(0.0f, 0.0f, 1.0f)
		shapes.add(Sphere(0.5f, 20, 20))
		moveLast(0.0f, 0.0f, -1.0f)
		shapes.add(Pyramid(0.5f))
		moveLast(0.0f, 0.0f, 1.0f)
		shapes.add(Cylinder(0.5f, 1.0f, 20))
		moveLast(0.0f, 0.0f, -1.0f)
		shapes.add(Cone(0.5f, 1.0f, 20))
		moveLast(0.0f, 0.0f, 1.0f)
	}

	private fun moveLast(x: Float, y: Float, z: Float) = shapes.last().translate(x, y, z)
	private fun rotateLast(x: Float, y: Float, z: Float) = shapes.last().rotate(x, y, z)
	private fun scaleLast(x: Float, y: Float, z: Float) = shapes.last().scale(x, y, z)

	fun draw(gl: GL10) = shapes.forEach { it.draw(gl) }
}
