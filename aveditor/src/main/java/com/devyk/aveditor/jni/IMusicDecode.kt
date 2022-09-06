package com.devyk.aveditor.jni

/**
 * <pre>
 *     author  : devyk on 2020-08-12 17:34
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IMusicDecode
 * </pre>
 */
interface IMusicDecode {
    /**
     * C++ 实现
     * 初始化解码器
     */
    fun addRecordMusic(musicPath: String?)

    /**
     * C++ 实现
     * 开始解码
     */
    fun start()

    /**
     * C++ 实现
     * 暂停解码
     */
    fun pause()

    /**
     * 恢复解码
     */
    fun resume()


    interface OnDecodeListener {

        /**
         * 开始解码
         */
        fun onDecodeStart(sampleRate: Int, channels: Int, sampleFormat: Int)

        /**
         * 回调编码完成的 PCM 数据
         */
        fun onDecodeData(data: ByteArray)

        /**
         * 解码完成
         */
        fun onDecodeStop()

    }

    fun addOnDecodeListener(listener: OnDecodeListener)

    /**
     * C++ 实现
     * 停止解码
     */
    fun stop()
}