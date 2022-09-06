package com.devyk.aveditor.jni

/**
 * <pre>
 *     author  : devyk on 2020-08-12 16:46
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is JNIManager
 * </pre>
 */
object JNIManager {


    /**
     * 播放模块
     */
    private var mPlayerEngine: IPlayer? = null

    /**
     * 音视频编辑模块
     */
    private var mAVEditor: IAVEditor? = null

    /**
     * 编解码边播放模块
     */
    private var mAVFileDecodeEngine: IMusicDecode? = null

    /**
     * FFmpeg 打包
     */
    private var mAVMuxer: INativeMuxer? = null

    /**
     * Java 打包
     */
    private var mAVJavaMuxer: IJavaMuxer? = null

    /**
     * 录制速率控制
     */
    private var mSpeedController: ISpeedController? = null


    init {
        System.loadLibrary("avcodec")
        System.loadLibrary("avutil")
        System.loadLibrary("swscale")
        System.loadLibrary("swresample")
        System.loadLibrary("avformat")
        System.loadLibrary("avfilter")
        System.loadLibrary("avdevice")
        System.loadLibrary("avtools")
        mPlayerEngine = AVPlayerEngine()
        mAVFileDecodeEngine = AVAudioDecodeEngine()
        mAVEditor = AVEditorEngine()
        mAVMuxer = AVMuxerEngine()
        mAVJavaMuxer = AVJavaMuxerEngine()
        mSpeedController = AVSpeedEngine()
    }


    /**
     * 动态替换播放拨快
     */
    fun <T : IPlayer> setPlayerEngine(t: T) {
        mPlayerEngine = t
    }

    /**
     * 动态替音频解码模块
     */
    fun <T : IMusicDecode> setAVFileDecodeEngine(t: T) {
        mAVFileDecodeEngine = t
    }

    /**
     * 动态替换媒体编辑模块
     */
    fun <T : IAVEditor> setAVFileDecodeEngine(t: T) {
        mAVEditor = t
    }

    /**
     * 动态替换复用器
     */
    fun <T : INativeMuxer> setAVMuxerEngine(t: T) {
        mAVMuxer = t
    }

    /**
     * 拿到播放的模块
     */
    fun getAVPlayEngine(): IPlayer? = mPlayerEngine

    /**
     * 拿到媒体解码模块
     */
    fun getAVDecodeEngine(): IMusicDecode? = mAVFileDecodeEngine


    /**
     * 拿到媒体编辑模块
     */
    fun getAVEditorEngine(): IAVEditor? = mAVEditor


    /**
     * 拿到媒体编辑模块
     */
    fun getAVMuxerEngine(): INativeMuxer? = mAVMuxer

    /**
     * 拿到媒体编辑模块
     */
    fun getAVJavaMuxer(): IJavaMuxer? = mAVJavaMuxer

    /**
     * 拿到速率控制
     */
    fun getAVSpeedEngine(): ISpeedController? = mSpeedController
}