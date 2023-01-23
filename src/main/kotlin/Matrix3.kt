import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.withSign

data class Matrix3 (
    val xx: Float, val yx: Float, val zx: Float,
    val xy: Float, val yy: Float, val zy: Float,
    val xz: Float, val yz: Float, val zz: Float
) {
    companion object {
        val ZERO = Matrix3(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val IDENTITY = Matrix3(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
    }

    // column getters
    val x get() = Vector3(xx, xy, xz)
    val y get() = Vector3(yx, yy, yz)
    val z get() = Vector3(zx, zy, zz)

    // row getters
    val xRow get() = Vector3(xx, yx, zx)
    val yRow get() = Vector3(xy, yy, zy)
    val zRow get() = Vector3(xz, yz, zz)

    operator fun unaryMinus(): Matrix3 = Matrix3(
        -xx, -yx, -zx,
        -xy, -yy, -zy,
        -xz, -yz, -zz)

    operator fun plus(that: Matrix3): Matrix3 = Matrix3(
        this.xx + that.xx, this.yx + that.yx, this.zx + that.zx,
        this.xy + that.xy, this.yy + that.yy, this.zy + that.zy,
        this.xz + that.xz, this.yz + that.yz, this.zz + that.zz)

    operator fun minus(that: Matrix3): Matrix3 = Matrix3(
        this.xx - that.xx, this.yx - that.yx, this.zx - that.zx,
        this.xy - that.xy, this.yy - that.yy, this.zy - that.zy,
        this.xz - that.xz, this.yz - that.yz, this.zz - that.zz)

    operator fun times(that: Float): Matrix3 = Matrix3(
        this.xx*that, this.yx*that, this.zx*that,
        this.xy*that, this.yy*that, this.zy*that,
        this.xz*that, this.yz*that, this.zz*that)

    operator fun times(that: Vector3): Vector3 = Vector3(
        this.xx*that.x + this.yx*that.y + this.zx*that.z,
        this.xy*that.x + this.yy*that.y + this.zy*that.z,
        this.xz*that.x + this.yz*that.y + this.zz*that.z)

    operator fun times(that: Matrix3): Matrix3 = Matrix3(
        this.xx*that.xx + this.yx*that.xy + this.zx*that.xz,
        this.xx*that.yx + this.yx*that.yy + this.zx*that.yz,
        this.xx*that.zx + this.yx*that.zy + this.zx*that.zz,
        this.xy*that.xx + this.yy*that.xy + this.zy*that.xz,
        this.xy*that.yx + this.yy*that.yy + this.zy*that.yz,
        this.xy*that.zx + this.yy*that.zy + this.zy*that.zz,
        this.xz*that.xx + this.yz*that.xy + this.zz*that.xz,
        this.xz*that.yx + this.yz*that.yy + this.zz*that.yz,
        this.xz*that.zx + this.yz*that.zy + this.zz*that.zz)

    /**
     * computes the square of the frobenius norm of this matrix
     * @return the frobenius norm squared
     */
    fun normSq(): Float = xx*xx + yx*yx + zx*zx + xy*xy + yy*yy + zy*zy + xz*xz + yz*yz + zz*zz

    /**
     * computes the frobenius norm of this matrix
     * @return the frobenius norm
     */
    fun norm(): Float = sqrt(normSq())

    /**
     * computes the determinant of this matrix
     * @return the determinant
     */
    fun det(): Float = (xz*yx - xx*yz)*zy + (xx*yy - xy*yx)*zz + (xy*yz - xz*yy)*zx

    /**
     * computes the trace of this matrix
     * @return the trace
     */
    fun trace(): Float = xx + yy + zz

    /**
     * computes the transpose of this matrix
     * @return the transpose matrix
     */
    fun transpose(): Matrix3 = Matrix3(
        xx, xy, xz,
        yx, yy, yz,
        zx, zy, zz)

    /**
     * computes the inverse of this matrix
     * @return the inverse matrix
     */
    fun inv(): Matrix3 {
        val det = det()
        return Matrix3(
            (yy*zz - yz*zy)/det, (yz*zx - yx*zz)/det, (yx*zy - yy*zx)/det,
            (xz*zy - xy*zz)/det, (xx*zz - xz*zx)/det, (xy*zx - xx*zy)/det,
            (xy*yz - xz*yy)/det, (xz*yx - xx*yz)/det, (xx*yy - xy*yx)/det)
    }

    operator fun div(that: Float): Matrix3 = this*(1f/that)

    /**
     * computes the right division, this * that^-1
     */
    operator fun div(that: Matrix3): Matrix3 = this*that.inv()

    /**
     * computes the inverse transpose of this matrix
     * @return the inverse transpose matrix
     */
    fun invTranspose(): Matrix3 {
        val det = det()
        return Matrix3(
            (yy*zz - yz*zy)/det, (xz*zy - xy*zz)/det, (xy*yz - xz*yy)/det,
            (yz*zx - yx*zz)/det, (xx*zz - xz*zx)/det, (xz*yx - xx*yz)/det,
            (yx*zy - yy*zx)/det, (xy*zx - xx*zy)/det, (xx*yy - xy*yx)/det)
    }

    /**
     * computes the nearest orthonormal matrix to this matrix
     * @return the rotation matrix
     */
    fun orthonormalize(): Matrix3 {
        var curMat = this
        var curDet = 1f/0f

        for (i in 1..100) {
            val newMat = (curMat + curMat.invTranspose())/2f
            val newDet = abs(newMat.det())
            // should almost always exit immediately
            if (newDet <= 1.0000001f || newDet >= curDet) return newMat
            curMat = newMat
            curDet = newDet
        }

        return curMat
    }

    /**
     * linearly interpolates this matrix to that matrix by t
     * @param that the matrix towards which to interpolate
     * @param t the amount by which to interpolate
     * @return the interpolated matrix
     */
    fun lerp(that: Matrix3, t: Float): Matrix3 = (1f - t)*this + t*that

    // assumes this matrix is orthonormal and converts this to a quaternion
    /**
     * creates a quaternion representing the same rotation as this matrix, assuming the matrix is a rotation matrix
     * @return the quaternion
     */
    fun toQuaternionAssumingOrthonormal(): Quaternion {
        if (this.det() <= 0f)
            throw Exception("Attempt to convert negative determinant matrix to quaternion")

        if (yy > -zz && zz > -xx && xx > -yy) {
            return Quaternion(1 + xx + yy + zz, yz - zy, zx - xz, xy - yx)
        } else if (xx > yy && xx > zz) {
            return Quaternion(yz - zy, 1 + xx - yy - zz, xy + yx, xz + zx)
        } else if (yy > zz) {
            return Quaternion(zx - xz, xy + yx, 1 - xx + yy - zz, yz + zy)
        } else {
            return Quaternion(xy - yx, xz + zx, yz + zy, 1 - xx - yy + zz)
        }
    }

    // orthogonalizes the matrix then returns the quaternioN
    /**
     * creates a quaternion representing the same rotation as this matrix
     * @return the quaternion
     */
    fun toQuaternion(): Quaternion = orthonormalize().toQuaternionAssumingOrthonormal()

    /**
     * creates an eulerAngles representing the same rotation as this matrix, assuming the matrix is a rotation matrix
     * @return the eulerAngles
     */
    fun toEulerAnglesAssumingOrthonormal(order: EulerOrder): EulerAngles {
        if (this.det() <= 0f)
            throw Exception("Attempt to convert negative determinant matrix to euler angles")

        val ETA = 1.57079632f
        if (order == EulerOrder.XYZ) {
            val kc = zy*zy + zz*zz
            if (kc == 0f) return EulerAngles(EulerOrder.XYZ, atan2(yz, yy), ETA.withSign(zx), 0f)

            return EulerAngles(
                EulerOrder.XYZ,
                atan2(          -zy,            zz),
                atan2(           zx,      sqrt(kc)),
                atan2(xy*zz - xz*zy, yy*zz - yz*zy)
            )
        } else if (order == EulerOrder.YZX) {
            val kc = xx*xx + xz*xz
            if (kc == 0f) return EulerAngles(EulerOrder.YZX, 0f, atan2(zx, zz), ETA.withSign(xy))

            return EulerAngles(
                EulerOrder.YZX,
                atan2(xx*yz - xz*yx, xx*zz - xz*zx),
                atan2(          -xz,            xx),
                atan2(           xy,      sqrt(kc))
            )
        } else if (order == EulerOrder.ZXY) {
            val kc = yy*yy + yx*yx
            if (kc == 0f) return EulerAngles(EulerOrder.ZXY, ETA.withSign(yz), 0f, atan2(xy, xx))

            return EulerAngles(
                EulerOrder.ZXY,
                atan2(           yz,      sqrt(kc)),
                atan2(yy*zx - yx*zy, yy*xx - yx*xy),
                atan2(          -yx,            yy)
            )
        } else if (order == EulerOrder.ZYX) {
            val kc = xy*xy + xx*xx
            if (kc == 0f) return EulerAngles(EulerOrder.ZYX, 0f, ETA.withSign(-xz), atan2(-yx, yy))

            return EulerAngles(
                EulerOrder.ZYX,
                atan2(zx*xy - zy*xx, yy*xx - yx*xy),
                atan2(          -xz,      sqrt(kc)),
                atan2(           xy,            xx)
            )
        } else if (order == EulerOrder.YXZ) {
            val kc = zx*zx + zz*zz
            if (kc == 0f) return EulerAngles(EulerOrder.YXZ, ETA.withSign(-zy), atan2(-xz, xx), 0f)

            return EulerAngles(
                EulerOrder.YXZ,
                atan2(          -zy,      sqrt(kc)),
                atan2(           zx,            zz),
                atan2(yz*zx - yx*zz, xx*zz - xz*zx)
            )
        } else if (order == EulerOrder.XZY) {
            val kc = yz*yz + yy*yy
            if (kc == 0f) return EulerAngles(EulerOrder.XZY, atan2(-zy, zz), 0f, ETA.withSign(-yx))

            return EulerAngles(
                EulerOrder.XZY,
                atan2(           yz,            yy),
                atan2(xy*yz - xz*yy, zz*yy - zy*yz),
                atan2(          -yx,      sqrt(kc))
            )
        } else {
            throw Exception("EulerAngles not implemented for given EulerOrder")
        }
    }

    // orthogonalizes the matrix then returns the euler angles
    /**
     * creates an eulerAngles representing the same rotation as this matrix
     * @return the eulerAngles
     */
    fun toEulerAngles(order: EulerOrder): EulerAngles = orthonormalize().toEulerAnglesAssumingOrthonormal(order)
}

operator fun Float.times(that: Matrix3): Matrix3 = that*this

operator fun Float.div(that: Matrix3): Matrix3 = that.inv()*this