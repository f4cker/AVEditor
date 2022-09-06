package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.devyk.aveditor.callback.IYUVDataListener
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.jni.IPlayer
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterType
import com.devyk.aveditor.video.renderer.AVEditorRenderer


/**
 * <pre>
 *     author  : devyk on 2020-05-21 16:51
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVEditorView 负责视频编辑的控件,在 Native 端进行软解码，没有传入 Surface 所以没有在 Native 渲染，
 *                转为 Java 端处理渲染，加滤镜编辑等工作
 * </pre>
 */
class AVEditorView : GLSurfaceView, IYUVDataListener, Runnable {
    private var mEditorRenderer: AVEditorRenderer? = null
    /**
     * 播放器
     */
    private var mIPlayer: IPlayer? = null


    private var TAG = this.javaClass.simpleName

    private var mPlayComplete = false

    private var mDataSource: String? = null

    private var isExit = false


    /**
     * 上一次获取的进度
     */
    var preTime = -1

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        isExit = false
        setEGLContextClientVersion(2)
        mEditorRenderer = AVEditorRenderer(getContext())
        setRenderer(mEditorRenderer)
        //主动刷新模式
//        renderMode = RENDERMODE_CONTINUOUSLY
        renderMode = RENDERMODE_WHEN_DIRTY
        mIPlayer = JNIManager.getAVPlayEngine()
        mIPlayer?.setYUVDataCallback(this)

        Thread(this).start()
    }

    /**
     * 设置编辑源
     */
    fun setEditSource(source: String?) {
        mDataSource = source
        mIPlayer?.setDataSource(source)
    }


    /**
     * 设置是否在 native 端渲染
     */
    fun setNativeRender(isNativeRender: Boolean) = mIPlayer?.setNativeRender(isNativeRender)

    /**
     * 播放
     */
    fun start() {
        mIPlayer?.setNativeRender(false)
        mIPlayer?.start()
    }

    /**
     * 停止
     */
    fun stop() {
        mIPlayer?.stop()
        isExit = true
    }

    /**
     * 播放进度
     */
    fun progress(): Double = mIPlayer?.progress()!!

    /**
     * 暂停
     */
    fun setPause(status: Boolean) = mIPlayer?.setPause(status)

    /**
     * 指定跳转到某个时间点播放
     */
    fun seekTo(seek: Double): Int? = mIPlayer?.seekTo(seek)

    override fun onYUV420pData(width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray) {
        mPlayComplete = false
        mEditorRenderer?.setYUVData(width, height, y, u, v)
        requestRender()
        //TODO----这里会导致后面有 2 s 数据没有刷新就seekTo 了
        val progress = progress()
        if (progress >= 100.0 && !mPlayComplete) {
            seekTo(0.0)
            mPlayComplete = true
            LogHelper.d(TAG, "progress:$progress")
            return
        }

    }


    /**
     * 内部包含已有的滤镜
     */
    fun setGPUImageFilter(type: AVFilterType?, listener: OnSelectFilterListener) {
        queueEvent {
            val gpuImageFilter = mEditorRenderer?.setGPUImageFilter(type)
            listener.onSelectFilter(gpuImageFilter)
        }
    }

    /**
     * 添加 GPUImage 滤镜
     */
    @Synchronized
    fun <gpuImageFilter : GPUImageFilter> setGPUImageFilter(filter: gpuImageFilter) {
        queueEvent {
            mEditorRenderer?.setGPUImageFilter(filter)
        }
    }

    /**
     * 添加水印
     */
    fun addWatermark(watermark: Watermark?) {
        mEditorRenderer?.addWatermark(watermark)
    }


    override fun run() {
//        while (true) {
//            if (isExit) {
//                return
//            }
//            var progress = progress().toInt()
//            if (preTime >= 100 && mEditorRenderer?.cache() == 0) {
//                LogHelper.d(TAG, "progress:$progress")
//                seekTo(0.0)
//            }
//
//            if (progress != preTime) {
//                LogHelper.d(TAG, "progress:$progress")
//                if (progress == 100)
//                    seekTo(0.0)
//            }
//            Thread.sleep(40)
//            preTime = progress;
//        }
    }

}