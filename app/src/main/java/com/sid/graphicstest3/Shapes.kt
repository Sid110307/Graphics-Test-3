package com.sid.graphicstest3

import java.nio.ByteBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

abstract class Shape {
	abstract fun draw(gl: GL10)

	abstract fun translate(x: Float, y: Float, z: Float)
	abstract fun rotate(x: Float, y: Float, z: Float)
	abstract fun scale(x: Float, y: Float, z: Float)
}

class Cube(private val size: Float) : Shape() {
	private val vertexBuffer = ByteBuffer.allocateDirect(8 * 3 * 4).run {
		asFloatBuffer().apply {
			put(
				floatArrayOf(
					-size, -size, -size,
					-size, -size, size,
					-size, size, -size,
					-size, size, size,
					size, -size, -size,
					size, -size, size,
					size, size, -size,
					size, size, size
				)
			)
			position(0)
		}
	}

	private val indexBuffer = ByteBuffer.allocateDirect(6 * 6 * 2).run {
		asShortBuffer().apply {
			put(
				shortArrayOf(
					0, 1, 2, 2, 1, 3,
					4, 5, 6, 6, 5, 7,
					0, 1, 4, 4, 1, 5,
					2, 3, 6, 6, 3, 7,
					0, 2, 4, 4, 2, 6,
					1, 3, 5, 5, 3, 7
				)
			)
			position(0)
		}
	}

	override fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

		gl.glDrawElements(GL10.GL_TRIANGLES, 6 * 6, GL10.GL_UNSIGNED_SHORT, indexBuffer)
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
	}

	override fun translate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(8 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 8) {
			vertexArray[i * 3] += x
			vertexArray[i * 3 + 1] += y
			vertexArray[i * 3 + 2] += z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun rotate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(8 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 8) {
			val x1 = vertexArray[i * 3]
			val y1 = vertexArray[i * 3 + 1]
			val z1 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x1 * cos(x) - y1 * sin(x)
			vertexArray[i * 3 + 1] = x1 * sin(x) + y1 * cos(x)
			vertexArray[i * 3 + 2] = z1

			val x2 = vertexArray[i * 3]
			val y2 = vertexArray[i * 3 + 1]
			val z2 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x2 * cos(y) - z2 * sin(y)
			vertexArray[i * 3 + 1] = y2
			vertexArray[i * 3 + 2] = x2 * sin(y) + z2 * cos(y)

			val x3 = vertexArray[i * 3]
			val y3 = vertexArray[i * 3 + 1]
			val z3 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x3
			vertexArray[i * 3 + 1] = y3 * cos(z) - z3 * sin(z)
			vertexArray[i * 3 + 2] = y3 * sin(z) + z3 * cos(z)
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(8 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 8) {
			vertexArray[i * 3] *= x
			vertexArray[i * 3 + 1] *= y
			vertexArray[i * 3 + 2] *= z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}
}

class Sphere(private val radius: Float, private val slices: Int, private val stacks: Int) :
	Shape() {
	private val vertexBuffer = ByteBuffer.allocateDirect((slices + 1) * (stacks + 1) * 3 * 4).run {
		asFloatBuffer().apply {
			val vertexArray = FloatArray((slices + 1) * (stacks + 1) * 3)
			var i = 0

			for (stackNumber in 0..stacks) {
				val stackSin = sin(stackNumber * Math.PI / stacks)
				val stackCos = cos(stackNumber * Math.PI / stacks)

				for (sliceNumber in 0..slices) {
					val sliceSin = sin(sliceNumber * 2 * Math.PI / slices)
					val sliceCos = cos(sliceNumber * 2 * Math.PI / slices)

					vertexArray[i++] = (radius * sliceCos * stackSin).toFloat()
					vertexArray[i++] = (radius * stackCos).toFloat()
					vertexArray[i++] = (radius * sliceSin * stackSin).toFloat()
				}
			}

			put(vertexArray)
			position(0)
		}
	}

	private val indexBuffer = ByteBuffer.allocateDirect(slices * stacks * 6 * 2).run {
		asShortBuffer().apply {
			val indexArray = ShortArray(slices * stacks * 6)
			var i = 0

			for (stackNumber in 0 until stacks) {
				for (sliceNumber in 0 until slices) {
					val first = (stackNumber * (slices + 1) + sliceNumber).toShort()
					val second = (first + slices + 1).toShort()

					indexArray[i++] = first
					indexArray[i++] = (first + 1).toShort()
					indexArray[i++] = second

					indexArray[i++] = (first + 1).toShort()
					indexArray[i++] = (second + 1).toShort()
					indexArray[i++] = second
				}
			}

			put(indexArray)
			position(0)
		}
	}

	override fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

		gl.glDrawElements(
			GL10.GL_TRIANGLES, slices * stacks * 6, GL10.GL_UNSIGNED_SHORT, indexBuffer
		)
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
	}

	override fun translate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * (stacks + 1) * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * (stacks + 1)) {
			vertexArray[i * 3] += x
			vertexArray[i * 3 + 1] += y
			vertexArray[i * 3 + 2] += z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun rotate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * (stacks + 1) * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * (stacks + 1)) {
			val x1 = vertexArray[i * 3]
			val y1 = vertexArray[i * 3 + 1]
			val z1 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x1 * cos(x) - y1 * sin(x)
			vertexArray[i * 3 + 1] = x1 * sin(x) + y1 * cos(x)
			vertexArray[i * 3 + 2] = z1

			val x2 = vertexArray[i * 3]
			val y2 = vertexArray[i * 3 + 1]
			val z2 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x2 * cos(y) - z2 * sin(y)
			vertexArray[i * 3 + 1] = y2
			vertexArray[i * 3 + 2] = x2 * sin(y) + z2 * cos(y)

			val x3 = vertexArray[i * 3]
			val y3 = vertexArray[i * 3 + 1]
			val z3 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x3
			vertexArray[i * 3 + 1] = y3 * cos(z) - z3 * sin(z)
			vertexArray[i * 3 + 2] = y3 * sin(z) + z3 * cos(z)
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * (stacks + 1) * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * (stacks + 1)) {
			vertexArray[i * 3] *= x
			vertexArray[i * 3 + 1] *= y
			vertexArray[i * 3 + 2] *= z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}
}

class Pyramid(private val size: Float) : Shape() {
	private val vertexBuffer = ByteBuffer.allocateDirect(5 * 3 * 4).run {
		asFloatBuffer().apply {
			put(
				floatArrayOf(
					-size, -size, -size,
					-size, -size, size,
					-size, size, -size,
					size, -size, -size,
					-size, size, size
				)
			)
			position(0)
		}
	}

	private val indexBuffer = ByteBuffer.allocateDirect(6 * 3 * 2).run {
		asShortBuffer().apply {
			put(
				shortArrayOf(
					0, 1, 2,
					0, 3, 2,
					0, 1, 4,
					1, 2, 4,
					2, 3, 4,
					3, 0, 4
				)
			)
			position(0)
		}
	}

	override fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

		gl.glDrawElements(GL10.GL_TRIANGLES, 6 * 3, GL10.GL_UNSIGNED_SHORT, indexBuffer)
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
	}

	override fun translate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(5 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 5) {
			vertexArray[i * 3] += x
			vertexArray[i * 3 + 1] += y
			vertexArray[i * 3 + 2] += z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun rotate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(5 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 5) {
			val x1 = vertexArray[i * 3]
			val y1 = vertexArray[i * 3 + 1]
			val z1 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x1 * cos(x) - y1 * sin(x)
			vertexArray[i * 3 + 1] = x1 * sin(x) + y1 * cos(x)
			vertexArray[i * 3 + 2] = z1

			val x2 = vertexArray[i * 3]
			val y2 = vertexArray[i * 3 + 1]
			val z2 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x2 * cos(y) - z2 * sin(y)
			vertexArray[i * 3 + 1] = y2
			vertexArray[i * 3 + 2] = x2 * sin(y) + z2 * cos(y)

			val x3 = vertexArray[i * 3]
			val y3 = vertexArray[i * 3 + 1]
			val z3 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x3
			vertexArray[i * 3 + 1] = y3 * cos(z) - z3 * sin(z)
			vertexArray[i * 3 + 2] = y3 * sin(z) + z3 * cos(z)
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray(5 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until 5) {
			vertexArray[i * 3] *= x
			vertexArray[i * 3 + 1] *= y
			vertexArray[i * 3 + 2] *= z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}
}

class Cylinder(private val radius: Float, private val height: Float, private val slices: Int) :
	Shape() {
	private val vertexBuffer = ByteBuffer.allocateDirect((slices + 1) * 2 * 3 * 4).run {
		asFloatBuffer().apply {
			val vertexArray = FloatArray((slices + 1) * 2 * 3)
			var i = 0

			for (sliceNumber in 0..slices) {
				val sliceSin = sin(sliceNumber * 2 * Math.PI / slices)
				val sliceCos = cos(sliceNumber * 2 * Math.PI / slices)

				vertexArray[i++] = (radius * sliceCos).toFloat()
				vertexArray[i++] = -height / 2
				vertexArray[i++] = (radius * sliceSin).toFloat()

				vertexArray[i++] = (radius * sliceCos).toFloat()
				vertexArray[i++] = height / 2
				vertexArray[i++] = (radius * sliceSin).toFloat()
			}

			put(vertexArray)
			position(0)
		}
	}

	private val indexBuffer = ByteBuffer.allocateDirect(slices * 6 * 2).run {
		asShortBuffer().apply {
			val indexArray = ShortArray(slices * 6)
			var i = 0

			for (sliceNumber in 0 until slices) {
				val first = (sliceNumber * 2).toShort()
				val second = (first + 1).toShort()

				indexArray[i++] = first
				indexArray[i++] = (first + 2).toShort()
				indexArray[i++] = second

				indexArray[i++] = (first + 2).toShort()
				indexArray[i++] = (second + 2).toShort()
				indexArray[i++] = second
			}

			put(indexArray)
			position(0)
		}
	}

	override fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

		gl.glDrawElements(GL10.GL_TRIANGLES, slices * 6, GL10.GL_UNSIGNED_SHORT, indexBuffer)
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
	}

	override fun translate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 2 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 2) {
			vertexArray[i * 3] += x
			vertexArray[i * 3 + 1] += y
			vertexArray[i * 3 + 2] += z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun rotate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 2 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 2) {
			val x1 = vertexArray[i * 3]
			val y1 = vertexArray[i * 3 + 1]
			val z1 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x1 * cos(x) - y1 * sin(x)
			vertexArray[i * 3 + 1] = x1 * sin(x) + y1 * cos(x)
			vertexArray[i * 3 + 2] = z1

			val x2 = vertexArray[i * 3]
			val y2 = vertexArray[i * 3 + 1]
			val z2 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x2 * cos(y) - z2 * sin(y)
			vertexArray[i * 3 + 1] = y2
			vertexArray[i * 3 + 2] = x2 * sin(y) + z2 * cos(y)

			val x3 = vertexArray[i * 3]
			val y3 = vertexArray[i * 3 + 1]
			val z3 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x3
			vertexArray[i * 3 + 1] = y3 * cos(z) - z3 * sin(z)
			vertexArray[i * 3 + 2] = y3 * sin(z) + z3 * cos(z)
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 2 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 2) {
			vertexArray[i * 3] *= x
			vertexArray[i * 3 + 1] *= y
			vertexArray[i * 3 + 2] *= z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}
}

class Cone(private val radius: Float, private val height: Float, private val slices: Int) :
	Shape() {
	private val vertexBuffer = ByteBuffer.allocateDirect((slices + 1) * 3 * 3 * 4).run {
		asFloatBuffer().apply {
			val vertexArray = FloatArray((slices + 1) * 3 * 3)
			var i = 0

			for (sliceNumber in 0..slices) {
				val sliceSin = sin(sliceNumber * 2 * Math.PI / slices)
				val sliceCos = cos(sliceNumber * 2 * Math.PI / slices)

				vertexArray[i++] = (radius * sliceCos).toFloat()
				vertexArray[i++] = -height / 2
				vertexArray[i++] = (radius * sliceSin).toFloat()

				vertexArray[i++] = 0f
				vertexArray[i++] = height / 2
				vertexArray[i++] = 0f

				vertexArray[i++] = (radius * sliceCos).toFloat()
				vertexArray[i++] = -height / 2
				vertexArray[i++] = (radius * sliceSin).toFloat()
			}

			put(vertexArray)
			position(0)
		}
	}

	private val indexBuffer = ByteBuffer.allocateDirect(slices * 6 * 2).run {
		asShortBuffer().apply {
			val indexArray = ShortArray(slices * 6)
			var i = 0

			for (sliceNumber in 0 until slices) {
				val first = (sliceNumber * 3).toShort()
				val second = (first + 1).toShort()

				indexArray[i++] = first
				indexArray[i++] = (first + 2).toShort()
				indexArray[i++] = second

				indexArray[i++] = (first + 2).toShort()
				indexArray[i++] = (second + 2).toShort()
				indexArray[i++] = second
			}

			put(indexArray)
			position(0)
		}
	}

	override fun draw(gl: GL10) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

		gl.glDrawElements(GL10.GL_TRIANGLES, slices * 6, GL10.GL_UNSIGNED_SHORT, indexBuffer)
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
	}

	override fun translate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 3 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 3) {
			vertexArray[i * 3] += x
			vertexArray[i * 3 + 1] += y
			vertexArray[i * 3 + 2] += z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun rotate(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 3 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 3) {
			val x1 = vertexArray[i * 3]
			val y1 = vertexArray[i * 3 + 1]
			val z1 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x1 * cos(x) - y1 * sin(x)
			vertexArray[i * 3 + 1] = x1 * sin(x) + y1 * cos(x)
			vertexArray[i * 3 + 2] = z1

			val x2 = vertexArray[i * 3]
			val y2 = vertexArray[i * 3 + 1]
			val z2 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x2 * cos(y) - z2 * sin(y)
			vertexArray[i * 3 + 1] = y2
			vertexArray[i * 3 + 2] = x2 * sin(y) + z2 * cos(y)

			val x3 = vertexArray[i * 3]
			val y3 = vertexArray[i * 3 + 1]
			val z3 = vertexArray[i * 3 + 2]

			vertexArray[i * 3] = x3
			vertexArray[i * 3 + 1] = y3 * cos(z) - z3 * sin(z)
			vertexArray[i * 3 + 2] = y3 * sin(z) + z3 * cos(z)
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		vertexBuffer.position(0)
		val vertexArray = FloatArray((slices + 1) * 3 * 3)
		vertexBuffer.get(vertexArray)
		vertexBuffer.position(0)

		for (i in 0 until (slices + 1) * 3) {
			vertexArray[i * 3] *= x
			vertexArray[i * 3 + 1] *= y
			vertexArray[i * 3 + 2] *= z
		}

		vertexBuffer.put(vertexArray)
		vertexBuffer.position(0)
	}
}
