package com.sid.graphicstest3

import android.opengl.EGL14
import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLU
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.pow

class CustomRenderer : GLSurfaceView.Renderer {
	private fun drawText(text: String, _x: Float, _y: Float, _z: Float) {
		val shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)

		GLES20.glShaderSource(shader, text)
		GLES20.glCompileShader(shader)

		val shaderProgram = GLES20.glCreateProgram()
		GLES20.glAttachShader(shaderProgram, shader)

		GLES20.glLinkProgram(shaderProgram)
		GLES20.glUseProgram(shaderProgram)

		GLES20.glUniform3f(GLES20.glGetUniformLocation(shaderProgram, "position"), _x, _y, _z)
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
	}

	override fun onDrawFrame(gl: GL10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

		GLES10.glLoadIdentity()
		GLU.gluLookAt(gl, x, y, z, lx, ly, lz, 0f, 1f, 0f)
		GLES10.glColor4f(1f, 1f, 1f, 1f)

		showGrid()
		showAxes()
		showHelp()

		GLES10.glPushMatrix()
		GLES10.glTranslatef(lx, ly, lz)
		GLES10.glColor4f(1f, 1f, 1f, 1f)
		drawSnowman(gl)
		GLES10.glPopMatrix()

		/*
		for (i in -SNOWMAN_SPREAD until SNOWMAN_SPREAD)
			for (j in -SNOWMAN_SPREAD until SNOWMAN_SPREAD) {
				glPushMatrix()
				glTranslatef(i * 2f, 0f, j * 2f)
				drawSnowman(gl)
				glPopMatrix()
			}
		*/

		GLES10.glColor4f(0f, 0f, 0f, 1f)
		drawText("Total snowmen: ${(SNOWMAN_SPREAD * 2.0).pow(2.0)}".trim(), -1f, 5f, 5f)

		EGL14.eglSwapBuffers(
			EGL14.eglGetCurrentDisplay(),
			EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW)
		)
	}

	private fun glVertex3f(x: Float, y: Float, z: Float) {
		return GLES20.glVertexAttribPointer(
			0,
			3,
			GLES20.GL_FLOAT,
			false,
			0,
			ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
				put(x)
				put(y)
				put(z)
				position(0)
			})
	}

	private fun showGrid() {
		var i = -25f

		while (i <= 25) {
			glVertex3f(i, -1f, -25f)
			glVertex3f(i, -1f, 25f)
			glVertex3f(-25f, -1f, i)
			glVertex3f(25f, -1f, i)

			i += 0.25f
		}
	}

	private fun showAxes() {
		GLES10.glColor4f(1f, 0f, 0f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(10f, 0f, 0f)

		GLES10.glColor4f(0f, 1f, 0f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(0f, 10f, 0f)

		GLES10.glColor4f(0f, 0f, 1f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(0f, 0f, 10f)
	}

	private fun showHelp() {
		GLES10.glColor4f(0f, 0f, 0f, 1f)

		drawText("One finger - pan", -1f, 0f, 5f)
		drawText("Two fingers - rotate", -1f, -0.25f, 5f)
		drawText("Three fingers - move", -1f, -0.5f, 5f)
		drawText("Four fingers - reset", -1f, -0.75f, 5f)
	}

	private fun drawSnowman(gl: GL10) {
		GLES10.glColor4f(1f, 1f, 1f, 1f)
		GLES10.glTranslatef(0f, 0.75f, 0f)
		Sphere(0.75f, 20, 20).draw(gl)

		GLES10.glTranslatef(0f, 1f, 0f)
		Sphere(0.25f, 20, 20).draw(gl)
		GLES10.glPushMatrix()

		GLES10.glColor4f(0f, 0f, 0f, 1f)
		GLES10.glTranslatef(0.05f, 0.10f, 0.18f)
		Sphere(0.05f, 20, 20).draw(gl)

		GLES10.glTranslatef(-0.1f, 0f, 0f)
		Sphere(0.05f, 20, 20).draw(gl)
		GLES10.glPopMatrix()

		GLES10.glColor4f(1f, 0.5f, 0.5f, 1f)
		GLES10.glRotatef(0f, 1f, 0f, 0f)
		Cone(0.08f, 0.5f, 10, 10).draw(gl)

		GLES10.glPushMatrix()
		GLES10.glTranslatef(0f, 0f, 0.5f)
		GLES10.glRotatef(90f, 1f, 0f, 0f)
		GLES10.glRotatef(90f, 0f, 1f, 0f)
		GLES10.glRotatef(90f, 0f, 0f, 1f)
		Cone(0.08f, 0.25f, 10, 10).draw(gl)
		GLES10.glPopMatrix()
	}

	override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
		GLES10.glViewport(0, 0, width, height)
		// TODO: Fix segfault here:
		GLES10.glMatrixMode(GLES10.GL_PROJECTION)
		GLES10.glLoadIdentity()

		GLU.gluPerspective(gl, 45f, (width * 1.0f / height), 1f, 1000f)
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW)
		GLES10.glLoadIdentity()
	}

	override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
		GLES10.glEnable(GLES10.GL_DEPTH_TEST)
		GLES10.glDepthFunc(GLES10.GL_LEQUAL)

		GLES10.glClearColor(0.529f, 0.808f, 0.922f, 0f)
		GLES10.glClearDepthf(1f)

		GLES10.glHint(GLES10.GL_PERSPECTIVE_CORRECTION_HINT, GLES10.GL_NICEST)
		GLES10.glDisable(GLES10.GL_DITHER)
	}

	companion object {
		const val SNOWMAN_SPREAD = 3

		@Volatile
		var angle = 0f

		var lx = 0f
		var ly = 0f
		var lz = -1f

		var x = 0f
		var y = 2.5f
		var z = 15f
	}
}
