package com.devyk.aveditor.controller

import android.media.MediaCodec
import android.media.MediaFormat
import com.devyk.aveditor.audio.AudioProcessor
import com.devyk.aveditor.callback.IController
import com.devyk.aveditor.callback.OnAudioEncodeListener
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.decode.FFmpegAudioDecode
import com.devyk.aveditor.decode.IAudioDecode
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.jni.IMusicDecode
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.mediacodec.AudioEncoder
import com.devyk.aveditor.utils.LogHelper
import java.nio.ByteBuffer
import java.util.*


/**
 * <pre>
 *     author  : devyk on 2020-07-15 22:05
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioController 音频采集和音频编码的控制
 * </pre>
 */
class AudioController(audioConfiguration: AudioConfiguration) : IController,
    AudioProcessor.OnRecordListener,
    OnAudioEncodeListener, IMusicDecode.OnDecodeListener {


    /**
     * 音频采集-》编解码 需要用到的默认参数
     */
    private var mAudioConfiguration = AudioConfiguration.createDefault()

    /**
     * 音频编解码用到的实体程序
     */
    private var mAudioEncoder: AudioEncoder

    /**
     * 音频采集用到的实体程序
     */
    private var mAudioProcessor: AudioProcessor

    /**
     * 从一个 Mp4,MP3 等文件中解码拿到 PCM 数据
     */
    private var mAudioDecode: IAudioDecode


    /**
     * 录制音频源，可以是一个本地文件，也可以是一个网络文件
     */
    private var mRecordAudioSource: String? = null

    /**
     * 音频数据的监听
     */
    private var mAudioDataListener: IController.OnAudioDataListener? = null

    private var mSpeedPcmData: ShortArray? = null

    /**
     * 默认的速率
     */
    private var mSpeed = Speed.NORMAL

    init {
        mAudioConfiguration = audioConfiguration
        mAudioProcessor = AudioProcessor()
        mAudioEncoder = AudioEncoder(mAudioConfiguration)
        mAudioDecode = FFmpegAudioDecode()
        mAudioProcessor.init(
            mAudioConfiguration.audioSource,
            mAudioConfiguration.frequency,
            mAudioConfiguration.channelCount
        )
        mAudioProcessor.addRecordListener(this)
        mAudioEncoder.setOnAudioEncodeListener(this)
        mAudioDecode.addOnDecodeListener(this)
    }


    /**
     * 触发 开始
     */
    override fun start(speed: Speed) {
        LogHelper.e("SORT->", "start mRecordAudioSource")
        mSpeed = speed
        mRecordAudioSource?.let {
            mAudioDecode.addRecordMusic(it)
            mAudioDecode.start()
            return
        }
        mAudioProcessor.startRcording()
    }

    /**
     * 触发 暂停
     */
    override fun pause() {
        mRecordAudioSource?.let {
            mAudioDecode.pause()
            return
        }
        mAudioProcessor.setPause(true)
    }

    /**
     * 触发恢复
     */
    override fun resume() {
        mRecordAudioSource?.let {
            mAudioDecode.resume()
            return
        }
        mAudioProcessor.setPause(false)
    }

    /**
     * 触发停止
     */
    override fun stop() {
        mRecordAudioSource?.let {
            mAudioDecode.stop()
            return
        }
        mAudioProcessor.stop()

        JNIManager.getAVSpeedEngine()?.close(0)
    }


    /**
     * 当采集 PCM 数据的时候返回
     */
    override fun onPcmData(byteArray: ByteArray) {
        if (mSpeed == Speed.NORMAL) {
            mAudioEncoder.enqueueCodec(byteArray)
            return
        }
        mSpeedPcmData?.let { Arrays.fill(it, 0) }
        JNIManager.getAVSpeedEngine()?.putData(0, byteArray, byteArray.size)
        while (true) {
            val outSize = JNIManager.getAVSpeedEngine()?.getData(0, mSpeedPcmData!!, byteArray.size)
            if (outSize == 0)
                break;
            mAudioEncoder.enqueueCodec(mSpeedPcmData?.copyOf(outSize!!))
        }
    }

    /**
     * 当开始采集
     */
    override fun onStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
        mSpeedPcmData = ShortArray(sampleRate * channels * 2)
        JNIManager.getAVSpeedEngine()?.initSpeedController(0, channels, sampleRate, mSpeed.value, 1.0)
        mAudioEncoder.start(mSpeed)
    }

    /**
     * 设置禁言
     */
    override fun setMute(isMute: Boolean) {
        super.setMute(isMute)
        mAudioProcessor.setMute(isMute)


    }

    override fun onStop() {
        super.onStop()
        mAudioEncoder.stop()
    }

    /**
     * 当采集出现错误
     */
    override fun onError(meg: String?) {
        mAudioDataListener?.onError(meg)
    }

    /**
     * 当 Audio 编码数据的时候
     */
    override fun onAudioEncode(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mAudioDataListener?.onAudioData(bb, bi)
    }

    /**
     * 编码的输出格式
     */
    override fun onAudioOutFormat(outputFormat: MediaFormat?) {
        mAudioDataListener?.onAudioOutformat(outputFormat)
    }

    fun setRecordAudioSource(recordAudioSource: String?) {
        LogHelper.e("SORT->", "mRecordAudioSource setRecordAudioSource")
        mRecordAudioSource = recordAudioSource

    }


    /**
     * 音频解码的回调
     */
    override fun onDecodeStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
        mSpeedPcmData = ShortArray(sampleRate * channels * 2)
        JNIManager.getAVSpeedEngine()?.initSpeedController(0, channels, sampleRate, mSpeed.value, 1.0)
        val build = AudioConfiguration.Builder().setChannelCount(channels).setFrequency(sampleRate).build()
        mAudioEncoder.setAudioConfiguration(build)
        mAudioEncoder.start(mSpeed)
    }

    /**
     * 这里是 C++ 解码传递过来的，是 PCM 数据
     */
    override fun onDecodeData(data: ByteArray) {
//        mAudioEncoder?.enqueueCodec(data)
        onPcmData(data)
    }

    override fun onDecodeStop() {
        onStop()
    }

    override fun setAudioDataListener(audioDataListener: IController.OnAudioDataListener) {
        super.setAudioDataListener(audioDataListener)
        mAudioDataListener = audioDataListener
    }

}