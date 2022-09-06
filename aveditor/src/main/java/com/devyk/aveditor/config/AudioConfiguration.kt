package com.devyk.aveditor.config

import android.media.AudioFormat
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaRecorder

/**
 * <pre>
 *     author  : devyk on 2020-06-13 15:26
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioConfiguration
 * </pre>
 */
class AudioConfiguration private constructor(builder: Builder) {

    val minBps: Int
    val maxBps: Int
    val frequency: Int
    val encoding: Int
    val codeType: CodeType
    val channelCount: Int
    val adts: Int
    val aacProfile: Int
    val mime: String
    val aec: Boolean
    val mediaCodec: Boolean
    val audioSource:Int

    init {
        minBps = builder.minBps
        maxBps = builder.maxBps
        frequency = builder.frequency
        encoding = builder.encoding
        codeType = builder.codeType
        channelCount = builder.channelCount
        adts = builder.adts
        mime = builder.mime
        aacProfile = builder.aacProfile
        aec = builder.aec
        mediaCodec = builder.mediaCodec
        audioSource = builder.audioSource
    }

    class Builder {
        var mediaCodec = DEFAULT_MEDIA_CODEC
        var minBps = DEFAULT_MIN_BPS
        var maxBps = DEFAULT_MAX_BPS
        var frequency = DEFAULT_FREQUENCY
        var encoding = DEFAULT_AUDIO_ENCODING
        var channelCount = DEFAULT_CHANNEL_COUNT
        var adts = DEFAULT_ADTS
        var mime = DEFAULT_MIME
        var codeType = DEFAULT_CODE_TYPE
        var aacProfile = DEFAULT_AAC_PROFILE
        var aec = DEFAULT_AEC
        var audioSource = DEFAULT_AUDIO_SOURCE

        fun setBps(minBps: Int, maxBps: Int): Builder {
            this.minBps = minBps
            this.maxBps = maxBps
            return this
        }

        fun setFrequency(frequency: Int): Builder {
            this.frequency = frequency
            return this
        }

        fun setEncoding(encoding: Int): Builder {
            this.encoding = encoding
            return this
        }

        fun setChannelCount(channelCount: Int): Builder {
            this.channelCount = channelCount
            return this
        }

        fun setAdts(adts: Int): Builder {
            this.adts = adts
            return this
        }

        fun setAacProfile(aacProfile: Int): Builder {
            this.aacProfile = aacProfile
            return this
        }

        fun setMime(mime: String): Builder {
            this.mime = mime
            return this
        }

        fun setAec(aec: Boolean): Builder {
            this.aec = aec
            return this
        }

        fun setMediaCodec(mediaCodec: Boolean): AudioConfiguration.Builder {
            this.mediaCodec = mediaCodec
            return this
        }

        fun setCodecType(codeType: CodeType): Builder {
            this.codeType = codeType
            return this
        }

        fun setAudioSource(source: Int): Builder {
            this.audioSource = source
            return this
        }

        fun build(): AudioConfiguration {
            return AudioConfiguration(this)
        }
    }

    companion object {
        const val DEFAULT_FREQUENCY = 44100
        const val DEFAULT_MAX_BPS = 64
        const val DEFAULT_MIN_BPS = 32
        const val DEFAULT_ADTS = 0
        val DEFAULT_CODE_TYPE = CodeType.ENCODE
        const val DEFAULT_MIME = MediaFormat.MIMETYPE_AUDIO_AAC
        const val DEFAULT_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val DEFAULT_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC
        const val DEFAULT_CHANNEL_COUNT = 1
        const val DEFAULT_AEC = false
        const val DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        const val DEFAULT_MEDIA_CODEC = true

        fun createDefault(): AudioConfiguration {
            return Builder().build()
        }
    }


    enum class CodeType(codeType: Int) {
        ENCODE(1),
        DECODE(2),
    }
}
