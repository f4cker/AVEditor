package com.devyk.aveditor.entity

/**
 * <pre>
 *     author  : devyk on 2020-10-11 14:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YUVEntity
 * </pre>
 */
data class YUVEntity(
    val width: Int,
    val height: Int,
    val y: ByteArray,
    val u: ByteArray,
    val v: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YUVEntity

        if (!y.contentEquals(other.y)) return false
        if (!u.contentEquals(other.u)) return false
        if (!v.contentEquals(other.v)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = y.contentHashCode()
        result = 31 * result + u.contentHashCode()
        result = 31 * result + v.contentHashCode()
        return result
    }
}