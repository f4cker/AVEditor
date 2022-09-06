package com.devyk.aveditor.jni

import com.devyk.aveditor.callback.IYUVDataListener
import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.utils.LogHelper

/**
 * <pre>
 *     author  : devyk on 2020-08-12 16:57
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVPlayerEngine
 * </pre>
 */
class AVPlayerEngine : IPlayer {

    private var mIYUVDataListener: IYUVDataListener? = null

    /**
     * 设置是否硬件解码
     */
    external override fun setMediaCodec(isMediacodec: Boolean)


    external override fun setPlayVolume(v: Int)

    override fun setPlaySpeed(speed: Speed) {
        setPlaySpeed(speed.value)
    }

    private external fun setPlaySpeed(speed: Double)

    /**
     * init 初始化
     */
    external override fun initSurface(surface: Any)

    /**
     * 设置播放源
     */
    external override fun setDataSource(source: String?)

    /**
     * 设置播放源
     */
    external override fun setDataSource(sources: ArrayList<MediaEntity>?)

    /**
     * 播放
     */
    external override fun start()

    /**
     * 播放
     */
    external override fun progress(): Double

    /**
     * 暂停
     */
    external override fun setPause(status: Boolean)

    /**
     * 指定跳转到某个时间点播放
     */
    external override fun seekTo(seek: Double): Int

    /**
     * 停止
     */
    external override fun stop()

    /**
     * 设置是否在 native 端进行渲染
     */
    external override fun setNativeRender(b: Boolean)


    /**
     * 接收来自 Native 发送过来的 YUV 数据
     */
    fun onReceiverFromNativeYUVData(
        width: Int,
        height: Int,
        y: ByteArray,
        u: ByteArray,
        v: ByteArray
    ) {
        LogHelper.d("onReceiverFromNativeYUVData", "width:$width height:$height")
        mIYUVDataListener?.onYUV420pData(width, height, y, u, v)
    }

    override fun setYUVDataCallback(listener: IYUVDataListener) {
        this.mIYUVDataListener = listener
    }
}