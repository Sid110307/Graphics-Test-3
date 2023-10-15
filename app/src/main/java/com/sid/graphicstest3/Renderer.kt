package com.sid.graphicstest3

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class Renderer : GLSurfaceView.Renderer {
	override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
		GLES32.glClearColor(0.55f, 0.75f, 1.0f, 1.0f)
		GLES32.glEnable(GLES32.GL_DEPTH_TEST)
		GLES32.glEnable(GLES32.GL_CULL_FACE)
		GLES32.glCullFace(GLES32.GL_BACK)
	}

	override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
		GLES32.glViewport(0, 0, width, height)
		MainActivity.camera.setAspectRatio(width.toFloat() / height.toFloat())
	}

	override fun onDrawFrame(gl: GL10) {
		GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
		MainActivity.camera.update()
		MainActivity.scene.draw(gl)
	}
}
