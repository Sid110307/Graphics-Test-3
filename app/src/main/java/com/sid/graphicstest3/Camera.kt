package com.sid.graphicstest3

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log

class Camera {
	private val projectionMatrix = FloatArray(16)
	private val viewMatrix = FloatArray(16)

	private var eyeX = 0f
	private var eyeY = 0f
	private var eyeZ = 0f

	private var centerX = 0f
	private var centerY = 0f
	private var centerZ = 0f

	private var upX = 0f
	private var upY = 1f
	private var upZ = 0f

	fun applyViewProjection(program: Int) {
		val projectionMatrixLocation = GLES32.glGetUniformLocation(program, "projectionMatrix")
		val viewMatrixLocation = GLES32.glGetUniformLocation(program, "viewMatrix")

		GLES32.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrix, 0)
		GLES32.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrix, 0)
	}

	fun setAspectRatio(aspectRatio: Float) {
		Matrix.frustumM(
			projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 1f, 10f
		)
	}

	fun pan(deltaX: Float, deltaY: Float) {
		eyeX -= deltaX / 100f
		eyeY += deltaY / 100f
		centerX -= deltaX / 100f
		centerY += deltaY / 100f

		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		Log.e("Camera", "Pan with deltaX: $deltaX, deltaY: $deltaY")
	}

	fun zoom(deltaDistance: Float) {
		eyeZ += deltaDistance / 100f
		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		Log.e("Camera", "Zoom with deltaDistance: $deltaDistance")
	}

	fun rotate(deltaX: Float, deltaY: Float) {
		eyeX -= deltaX / 100f
		eyeY += deltaY / 100f

		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		Log.e("Camera", "Rotate with deltaX: $deltaX, deltaY: $deltaY")
	}

	fun reset() {
		eyeX = 0f
		eyeY = 0f
		eyeZ = 0f
		centerX = 0f
		centerY = 0f
		centerZ = 0f
		upX = 0f
		upY = 1f
		upZ = 0f

		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		Log.e("Camera", "Reset camera")
	}

	fun update() {
		setViewMatrix(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	}

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
}
