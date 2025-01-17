package com.devyk.aveditor.mediacodec

import android.media.MediaCodec

import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.entity.Speed

import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-06-15 21:42
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ICodec
 * </pre>
 */

interface IVideoCodec {


    /**
     * 初始化编码器
     */
    fun prepare(videoConfiguration: VideoConfiguration = VideoConfiguration.createDefault()) {}

    /**
     * start 编码
     */
    fun start(speed: Speed)

    /**
     * 停止编码
     */
    fun stop()

    /**
     * 返回编码好的 H264 数据
     */
    fun onVideoEncode(bb: ByteBuffer?, mBufferInfo: MediaCodec.BufferInfo)


}