/*
 * MIT License
 * Copyright (c) 2022, Donald F Reynolds
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import kotlin.math.cos
import kotlin.math.sin

enum class EulerOrder {XYZ, YZX, ZXY, ZYX, YXZ, XZY}

// prefer Y.toX
// but if ambiguous, use X.fromY
/*
 * Euler Angles contains both the x y z angle parameters and the order of application
 */
data class EulerAngles(val order: EulerOrder, val x: Float, val y: Float, val z: Float) {
    /**
     * creates a quaternion which represents the same rotation as this eulerAngles
     * @return the quaternion
     */
    fun toQuaternion(): Quaternion {
        val cX = cos(x/2f)
        val cY = cos(y/2f)
        val cZ = cos(z/2f)
        val sX = sin(x/2f)
        val sY = sin(y/2f)
        val sZ = sin(z/2f)

        return when (order) {
            EulerOrder.XYZ -> Quaternion(
                cX*cY*cZ - sX*sY*sZ,
                cY*cZ*sX + cX*sY*sZ,
                cX*cZ*sY - cY*sX*sZ,
                cZ*sX*sY + cX*cY*sZ)
            EulerOrder.YZX -> Quaternion(
                cX*cY*cZ - sX*sY*sZ,
                cY*cZ*sX + cX*sY*sZ,
                cX*cZ*sY + cY*sX*sZ,
                cX*cY*sZ - cZ*sX*sY)
            EulerOrder.ZXY -> Quaternion(
                cX*cY*cZ - sX*sY*sZ,
                cY*cZ*sX - cX*sY*sZ,
                cX*cZ*sY + cY*sX*sZ,
                cZ*sX*sY + cX*cY*sZ)
            EulerOrder.ZYX -> Quaternion(
                cX*cY*cZ + sX*sY*sZ,
                cY*cZ*sX - cX*sY*sZ,
                cX*cZ*sY + cY*sX*sZ,
                cX*cY*sZ - cZ*sX*sY)
            EulerOrder.YXZ -> Quaternion(
                cX*cY*cZ + sX*sY*sZ,
                cY*cZ*sX + cX*sY*sZ,
                cX*cZ*sY - cY*sX*sZ,
                cX*cY*sZ - cZ*sX*sY)
            EulerOrder.XZY -> Quaternion(
                cX*cY*cZ + sX*sY*sZ,
                cY*cZ*sX - cX*sY*sZ,
                cX*cZ*sY - cY*sX*sZ,
                cZ*sX*sY + cX*cY*sZ)
        }
    }

    // temp, replace with direct conversion later
    //fun toMatrix(): Matrix3 = this.toQuaternion().toMatrix()
    /**
     * creates a matrix which represents the same rotation as this eulerAngles
     * @return the matrix
     */
    fun toMatrix(): Matrix3 {
        val cX = cos(x)
        val cY = cos(y)
        val cZ = cos(z)
        val sX = sin(x)
        val sY = sin(y)
        val sZ = sin(z)

        return when (order) {
            EulerOrder.XYZ -> Matrix3(
                           cY*cZ,           -cY*sZ,     sY,
                cZ*sX*sY + cX*sZ, cX*cZ - sX*sY*sZ, -cY*sX,
                sX*sZ - cX*cZ*sY, cZ*sX + cX*sY*sZ,  cX*cY)
            EulerOrder.YZX -> Matrix3(
                 cY*cZ, sX*sY - cX*cY*sZ, cX*sY + cY*sX*sZ,
                    sZ,            cX*cZ,           -cZ*sX,
                -cZ*sY, cY*sX + cX*sY*sZ, cX*cY - sX*sY*sZ)
            EulerOrder.ZXY -> Matrix3(
                cY*cZ - sX*sY*sZ, -cX*sZ, cZ*sY + cY*sX*sZ,
                cZ*sX*sY + cY*sZ,  cX*cZ, sY*sZ - cY*cZ*sX,
                          -cX*sY,     sX,            cX*cY)
            EulerOrder.ZYX -> Matrix3(
                cY*cZ, cZ*sX*sY - cX*sZ, cX*cZ*sY + sX*sZ,
                cY*sZ, cX*cZ + sX*sY*sZ, cX*sY*sZ - cZ*sX,
                  -sY,            cY*sX,            cX*cY)
            EulerOrder.YXZ -> Matrix3(
                cY*cZ + sX*sY*sZ, cZ*sX*sY - cY*sZ, cX*sY,
                           cX*sZ,            cX*cZ,   -sX,
                cY*sX*sZ - cZ*sY, cY*cZ*sX + sY*sZ, cX*cY)
            EulerOrder.XZY -> Matrix3(
                           cY*cZ,   -sZ,            cZ*sY,
                sX*sY + cX*cY*sZ, cX*cZ, cX*sY*sZ - cY*sX,
                cY*sX*sZ - cX*sY, cZ*sX, cX*cY + sX*sY*sZ)
        }
    }
}