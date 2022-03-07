package sensors_in_paradise.sonar.custom_views.stickman.math

import android.opengl.Matrix
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

class Matrix4x4(private val data: FloatArray) {
    constructor() : this(arrayOf(1f,0f,0f,0f),arrayOf(0f,1f,0f,0f),arrayOf(0f,0f,1f,0f),arrayOf(0f,0f,0f,1f))
    constructor(col1: Array<Float>, col2: Array<Float>,col3: Array<Float>,col4: Array<Float>): this(FloatArray(16).apply {
        val a = col1 + col2 + col3 +col4
        for(i in 0 until 16){
           this[i] = a[i]
        }
    })
    init{
        if(data.size!=16){
            throw Exception("Matrix data size is invalid. Must be 16 but is ${data.size}")
        }
    }
    fun getCol(col: Int): FloatArray{
        return when(col){
            0->data.copyOfRange(0,4)
            1->data.copyOfRange(4,8)
            2->data.copyOfRange(8,12)
            3->data.copyOfRange(12,16)
            else -> throw IndexOutOfBoundsException("Col-Index must be 0 <= index <= 3")
        }
    }
    operator fun get(row: Int, col: Int): Float{
        return data[col*4+row]
    }

    operator fun set(row: Int, col: Int, value: Float){
        data[col*4+row] = value
    }
    fun getRow(row: Int): FloatArray{
        return FloatArray(4).apply {
            this[0]=this@Matrix4x4[0,row]
            this[1]=this@Matrix4x4[1,row]
            this[2]=this@Matrix4x4[2,row]
            this[3]=this@Matrix4x4[3,row]}
    }

    operator fun times(p: Vec4): Vec4 {
        val res = Vec4()
        for(row in 0..3){
            var sum = 0f
            for(col in 0..3){
                sum += this[row,col] * p[col]
            }
            res[row] = sum
        }
        return res
    }
    operator fun times(m: Matrix4x4): Matrix4x4 {
        val data = FloatArray(16)
        Matrix.multiplyMM(data, 0, this.data, 0, m.data, 0)
        return Matrix4x4(data)
    }

    fun clone(): Matrix4x4 {

        return Matrix4x4(
            data.clone()
        )
    }
    fun lookAt(eye: Vec3,
               center: Vec3,
               up: Vec3){
        lookAt(eye.x,eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z,this)
    }

    companion object{
        fun fromRows(row1: Array<Float>, row2: Array<Float>,row3: Array<Float>,row4: Array<Float>): Matrix4x4{
            val data = FloatArray(16)
            for(i in 0..3){
                data[i * 4 + 0] = row1[i]
                data[i * 4 + 1] = row2[i]
                data[i * 4 + 2] = row3[i]
                data[i * 4 + 3] = row4[i]
            }
            return Matrix4x4(data)
        }
        fun lookAt(eyeX: Float, eyeY: Float, eyeZ: Float,
                   centerX: Float, centerY: Float, centerZ: Float,
                   upX: Float, upY: Float, upZ: Float,  m: Matrix4x4?=null): Matrix4x4{

            val data = m?.data ?: FloatArray(16)
            Matrix.setLookAtM(data, 0, eyeX, eyeY, eyeZ, centerX,centerY,centerZ,upX, upY, upZ)
            return m ?: Matrix4x4(data)
        }
        fun lookAt(eye: Vec3,
                   center: Vec3,
                   up: Vec3, m: Matrix4x4?=null): Matrix4x4{
           return lookAt(eye.x,eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z,m)
        }
        fun project(fovy: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4x4{
            val data = FloatArray(16)
            Matrix.perspectiveM(data, 0,fovy, aspect, zNear, zFar )
            return Matrix4x4(data)
        }
        fun rotateY(angle: Float): Matrix4x4{
            val data = FloatArray(16)
            Matrix.rotateM(data, 0,angle, 0f, 1f, 0f )
            return Matrix4x4(data)
        }
    }
}