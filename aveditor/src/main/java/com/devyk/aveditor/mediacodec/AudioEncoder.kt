package com.devyk.aveditor.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import com.devyk.aveditor.callback.OnAudioEncodeListener
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.entity.Speed

import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-06-13 16:08
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioEncoder AAC 编码
 * </pre>
 */

class AudioEncoder(mAudioConfiguration: AudioConfiguration?) : BaseAudioCodec(mAudioConfiguration) {

    override fun onAudioOutFormat(outputFormat: MediaFormat?) {
        mListener?.onAudioOutFormat(outputFormat)
    }

    var mListener: OnAudioEncodeListener? = null

    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mListener?.onAudioEncode(bb, bi)
    }


    fun setOnAudioEncodeListener(listener: OnAudioEncodeListener?) {
        mListener = listener
    }

    @Synchronized
    override fun start(mSpeed: Speed) {
        super.start(mSpeed)
    }

    override fun stop() {
        super.stop()
        mListener = null
    }
}
