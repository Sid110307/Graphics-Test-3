package com.sid.graphicstest3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent

class MainActivity : Activity() {
	private lateinit var glView: GLSurfaceView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		glView = GraphicsGLView(this)
		setContentView(glView)
	}
}

class GraphicsGLView(context: Context) : GLSurfaceView(context) {
	init {
		setEGLContextClientVersion(2)
		setRenderer(CustomRenderer())

		renderMode = RENDERMODE_WHEN_DIRTY
	}

	private var lastX = 0f
	private var lastY = 0f

	/*
	One finger - pan
	Two fingers - rotate
	Three fingers - zoom
	Four fingers - reset
	*/
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		val x = event.x
		val y = event.y

		when (event.action) {
			MotionEvent.ACTION_MOVE -> {
				val fraction = 0.1f
				Log.d("MainActivity", "x: $x, y: $y")

				when (event.pointerCount) {
					4 -> {
						CustomRenderer.x = 0f
						CustomRenderer.y = 2.5f
						CustomRenderer.z = 15f

						CustomRenderer.lx = 0f
						CustomRenderer.ly = 0f
						CustomRenderer.lz = -1f

						CustomRenderer.angle = 0f
						Log.d("MainActivity", "Reset")
					}

					3 -> {
						CustomRenderer.lz += fraction * (y - lastY)
						Log.d("MainActivity", "Zoom")
					}

					2 -> {
						CustomRenderer.angle += fraction * (x - lastX)
						Log.d("MainActivity", "Rotate")
					}

					1 -> {
						CustomRenderer.x += CustomRenderer.lz * fraction * (x - lastX)
						CustomRenderer.z += CustomRenderer.lx * fraction * (y - lastY)
						Log.d("MainActivity", "Pan")
					}
				}
			}

			MotionEvent.ACTION_UP -> {
				lastX = x
				lastY = y
			}
		}

		return true
	}
}
