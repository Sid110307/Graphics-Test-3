package com.sid.graphicstest3

import android.opengl.Matrix

class Camera {
	private val projectionMatrix = FloatArray(16)
	private val viewMatrix = FloatArray(16)

	private var eyeX = 0.0f
	private var eyeY = 0.0f
	private var eyeZ = 0.0f

	private var centerX = 0.0f
	private var centerY = 0.0f
	private var centerZ = 0.0f

	private var upX = 0.0f
	private var upY = 0.0f
	private var upZ = 0.0f

	init {
		setViewMatrix(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f)
		setProjectionMatrix(45.0f, 1.0f, 0.001f, 100.0f)
	}

	fun setAspectRatio(aspectRatio: Float) = setProjectionMatrix(45.0f, aspectRatio, 0.001f, 100.0f)

	fun pan(deltaX: Float, deltaY: Float) {
		eyeX -= deltaX * 0.01f
		eyeY += deltaY * 0.01f
		centerX -= deltaX * 0.01f
		centerY += deltaY * 0.01f

		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	}

	fun zoom(deltaDistance: Float) {
		eyeZ += deltaDistance * 0.01f
		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	}

	fun rotate(deltaX: Float, deltaY: Float) {
		centerX -= deltaX * 0.01f
		centerY += deltaY * 0.01f

		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	}

	fun reset() = setViewMatrix(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f)
	fun update() = setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)

	private fun setViewMatrix(
		eyeX: Float,
		eyeY: Float,
		eyeZ: Float,
		centerX: Float,
		centerY: Float,
		centerZ: Float,
		upX: Float,
		upY: Float,
		upZ: Float,
	) {
		this.eyeX = eyeX
		this.eyeY = eyeY
		this.eyeZ = eyeZ

		this.centerX = centerX
		this.centerY = centerY
		this.centerZ = centerZ

		this.upX = upX
		this.upY = upY
		this.upZ = upZ

		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	}

	private fun setProjectionMatrix(fov: Float, aspectRatio: Float, near: Float, far: Float) =
		Matrix.perspectiveM(projectionMatrix, 0, fov, aspectRatio, near, far)
}
