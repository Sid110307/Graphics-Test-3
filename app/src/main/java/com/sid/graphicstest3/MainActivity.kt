package com.sid.graphicstest3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.opengl.EGL14.*
import android.opengl.GLES10.*
import android.opengl.GLES20
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLSurfaceView
import android.opengl.GLU.gluLookAt
import android.opengl.GLU.gluPerspective
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_LINES

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

	/*One finger - pan
Two fingers - rotate
Three fingers - zoom
Four fingers - reset*/
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		val x = event.x
		val y = event.y

		when (event.action) {
			MotionEvent.ACTION_MOVE -> {
				val fraction = 0.1f
				Log.e("MainActivity", "x: $x, y: $y")

				when (event.pointerCount) {
					4 -> {
						CustomRenderer.x = 0f
						CustomRenderer.y = 2.5f
						CustomRenderer.z = 15f

						CustomRenderer.lx = 0f
						CustomRenderer.ly = 0f
						CustomRenderer.lz = -1f

						CustomRenderer.angle = 0f
						Log.e("MainActivity", "Reset")
					}
					3 -> {
						CustomRenderer.lz += fraction * (y - lastY)
						Log.e("MainActivity", "Zoom")
					}
					2 -> {
						CustomRenderer.angle += fraction * (x - lastX)
						Log.e("MainActivity", "Rotate")
					}
					1 -> {
						CustomRenderer.x += CustomRenderer.lz * fraction * (x - lastX)
						CustomRenderer.z += CustomRenderer.lx * fraction * (y - lastY)
						Log.e("MainActivity", "Pan")
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
		GLES20.glDrawArrays(GL_LINES, 0, 2)
	}

	override fun onDrawFrame(gl: GL10) {
		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

		glLoadIdentity()
		gluLookAt(gl, x, y, z, lx, ly, lz, 0f, 1f, 0f)
		glColor4f(1f, 1f, 1f, 1f)

		showGrid()
		showAxes()
		showHelp()

		glPushMatrix()
		glTranslatef(lx, ly, lz)
		glColor4f(1f, 1f, 1f, 1f)
		drawSnowman(gl)
		glPopMatrix()

		/*for (i in -SNOWMAN_SPREAD until SNOWMAN_SPREAD)
			for (j in -SNOWMAN_SPREAD until SNOWMAN_SPREAD) {
				glPushMatrix()
				glTranslatef(i * 2f, 0f, j * 2f)
				drawSnowman(gl)
				glPopMatrix()
			}*/

		val txt = "Total snowmen: ${SNOWMAN_SPREAD * 2 * (SNOWMAN_SPREAD * 2)}".trim()
		glColor4f(0f, 0f, 0f, 1f)
		drawText(txt, -1f, 5f, 5f)

		eglSwapBuffers(eglGetCurrentDisplay(), eglGetCurrentSurface(EGL_READ))
	}

	private fun glVertex3f(x: Float, y: Float, z: Float) {
		return glVertexAttribPointer(
			0,
			3,
			GL_FLOAT,
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
		glColor4f(1f, 0f, 0f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(10f, 0f, 0f)

		glColor4f(0f, 1f, 0f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(0f, 10f, 0f)

		glColor4f(0f, 0f, 1f, 1f)
		glVertex3f(0f, 0f, 0f)
		glVertex3f(0f, 0f, 10f)
	}

	private fun showHelp() {
		glColor4f(0f, 0f, 0f, 1f)

		drawText("One finger - pan", -1f, 0f, 5f)
		drawText("Two fingers - rotate", -1f, -0.25f, 5f)
		drawText("Three fingers - move", -1f, -0.5f, 5f)
		drawText("Four fingers - reset", -1f, -0.75f, 5f)
	}

	private fun drawSnowman(gl: GL10) {
		glColor4f(1f, 1f, 1f, 1f)
		glTranslatef(0f, 0.75f, 0f)
		Sphere(0.75f, 20, 20).draw(gl)

		glTranslatef(0f, 1f, 0f)
		Sphere(0.25f, 20, 20).draw(gl)
		glPushMatrix()

		glColor4f(0f, 0f, 0f, 1f)
		glTranslatef(0.05f, 0.10f, 0.18f)
		Sphere(0.05f, 20, 20).draw(gl)

		glTranslatef(-0.1f, 0f, 0f)
		Sphere(0.05f, 20, 20).draw(gl)
		glPopMatrix()

		glColor4f(1f, 0.5f, 0.5f, 1f)
		glRotatef(0f, 1f, 0f, 0f)
		Cone(0.08f, 0.5f, 10, 10).draw(gl)

		glPushMatrix()
		glTranslatef(0f, 0f, 0.5f)
		glRotatef(90f, 1f, 0f, 0f)
		glRotatef(90f, 0f, 1f, 0f)
		glRotatef(90f, 0f, 0f, 1f)
		Cone(0.08f, 0.25f, 10, 10).draw(gl)
		glPopMatrix()
	}

	override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
		val ratio: Double = width * 1.0 / height

		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		glViewport(0, 0, width, height)

		gluPerspective(gl, 45f, ratio.toFloat(), 1f, 1000f)
		glMatrixMode(GL_MODELVIEW)
	}

	override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
		glEnable(GL_DEPTH_TEST)

		glClearColor(0.529f, 0.808f, 0.922f, 0f)
		glColor4f(1f, 1f, 1f, 1f)
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