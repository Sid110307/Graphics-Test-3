package com.sid.graphicstest3

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class Sphere(radius: Float, slices: Int, stacks: Int) {
	private val vertices: FloatBuffer
	private val normals: FloatBuffer
	private val texCoordinates: FloatBuffer
	private val indices: ShortBuffer
	private val indexCount: Int

	init {
		val vertexData = ArrayList<Float>()
		val normalData = ArrayList<Float>()
		val texCoordinatesData = ArrayList<Float>()
		val indexData = ArrayList<Short>()

		val phiStep = Math.PI / stacks.toFloat()
		val thetaStep = 2.0f * Math.PI / slices.toFloat()

		for (i in 0 until stacks) {
			val phi = i * phiStep

			for (j in 0 until slices) {
				val theta = j * thetaStep

				val x = radius * sin(phi) * cos(theta)
				val y = radius * cos(phi)
				val z = radius * sin(phi) * sin(theta)

				val u = 1.0f - j / slices.toFloat()
				val v = 1.0f - i / stacks.toFloat()

				vertexData.add(x.toFloat())
				vertexData.add(y.toFloat())
				vertexData.add(z.toFloat())

				normalData.add(x.toFloat())
				normalData.add(y.toFloat())
				normalData.add(z.toFloat())

				texCoordinatesData.add(u)
				texCoordinatesData.add(v)

				if (i < stacks - 1 && j < slices - 1) {
					val a = i * slices + j
					val b = i * slices + j + 1
					val c = (i + 1) * slices + j
					val d = (i + 1) * slices + j + 1

					indexData.add(a.toShort())
					indexData.add(b.toShort())
					indexData.add(c.toShort())
					indexData.add(c.toShort())
					indexData.add(b.toShort())
					indexData.add(d.toShort())
				}
			}
		}

		vertices = ByteBuffer.allocateDirect(vertexData.size * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer()
		vertices.put(vertexData.toFloatArray())
		vertices.position(0)

		normals = ByteBuffer.allocateDirect(normalData.size * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer()
		normals.put(normalData.toFloatArray())
		normals.position(0)

		texCoordinates =
			ByteBuffer.allocateDirect(texCoordinatesData.size * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer()
		texCoordinates.put(texCoordinatesData.toFloatArray())
		texCoordinates.position(0)

		indices = ByteBuffer.allocateDirect(indexData.size * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer()
		indices.put(indexData.toShortArray())
		indices.position(0)

		indexCount = indexData.size
	}

	fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices)
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals)
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates)

		gl.glDrawElements(GL10.GL_TRIANGLES, indexCount, GL10.GL_UNSIGNED_SHORT, indices)

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
	}
}

class Cone(baseRadius: Float, @Suppress("UNUSED_PARAMETER") height: Float, slices: Int, stacks: Int) {
	private val vertices: FloatBuffer
	private val normals: FloatBuffer
	private val texCoordinates: FloatBuffer
	private val indices: ShortBuffer
	private val indexCount: Int

	init {
		val vertexData = ArrayList<Float>()
		val normalData = ArrayList<Float>()
		val texCoordinatesData = ArrayList<Float>()
		val indexData = ArrayList<Short>()

		val phiStep = Math.PI / stacks.toFloat()
		val thetaStep = 2.0f * Math.PI / slices.toFloat()

		for (i in 0 until stacks) {
			val phi = i * phiStep

			for (j in 0 until slices) {
				val theta = j * thetaStep

				val x = baseRadius * sin(phi) * cos(theta)
				val y = baseRadius * cos(phi)
				val z = baseRadius * sin(phi) * sin(theta)

				val u = 1.0f - j / slices.toFloat()
				val v = 1.0f - i / stacks.toFloat()

				vertexData.add(x.toFloat())
				vertexData.add(y.toFloat())
				vertexData.add(z.toFloat())

				normalData.add(x.toFloat())
				normalData.add(y.toFloat())
				normalData.add(z.toFloat())

				texCoordinatesData.add(u)
				texCoordinatesData.add(v)

				if (i < stacks - 1 && j < slices - 1) {
					val a = i * slices + j
					val b = i * slices + j + 1
					val c = (i + 1) * slices + j
					val d = (i + 1) * slices + j + 1

					indexData.add(a.toShort())
					indexData.add(b.toShort())
					indexData.add(c.toShort())
					indexData.add(c.toShort())
					indexData.add(b.toShort())
					indexData.add(d.toShort())
				}
			}
		}

		vertices = ByteBuffer.allocateDirect(vertexData.size * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer()
		vertices.put(vertexData.toFloatArray())
		vertices.position(0)

		normals = ByteBuffer.allocateDirect(normalData.size * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer()
		normals.put(normalData.toFloatArray())
		normals.position(0)

		texCoordinates =
			ByteBuffer.allocateDirect(texCoordinatesData.size * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer()
		texCoordinates.put(texCoordinatesData.toFloatArray())
		texCoordinates.position(0)

		indices = ByteBuffer.allocateDirect(indexData.size * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer()
		indices.put(indexData.toShortArray())
		indices.position(0)

		indexCount = indexData.size
	}

	fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices)
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals)
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordinates)

		gl.glDrawElements(GL10.GL_TRIANGLES, indexCount, GL10.GL_UNSIGNED_SHORT, indices)

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
	}
}
