package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.MediaEntity


/**
 * <pre>
 *     author  : devyk on 2020-08-15 23:02
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVMergeEngine
 * </pre>
 */
class AVEditorEngine : IAVEditor {

    /**
     * 开始进行合并
     * @param outPath 输出的音视频文件
     * @param mediaFormat 输出的媒体格式类型
     */
    external override fun avStartMerge(
        inSource: ArrayList<MediaEntity>?,
        outPath: String,
        mediaFormat: String
    )

    /**
     * 合并的进度
     * @return 百分号
     */
    external override fun avMergeProgress(): Int

    /**
     * 添加一个音视频文件到将要合并的队列中
     */
    external override fun addAVFile(path: MediaEntity)

    /**
     * 插入一个媒体到 index 位置
     */
    external override fun insertAVFile(index: Int, media: MediaEntity)

    /**
     * 添加多个媒体文件
     */
    external override fun addAVFiles(medias: ArrayList<MediaEntity>)

    /**
     * 插入多个媒体文件
     */
    external override fun insertAVFiles(index: Int, path: ArrayList<MediaEntity>)

    /**
     * 添加配乐
     */
    external override fun addMusicFile(
        startTime: Long,
        endTime: Long,
        path: String,
        bgVolume: Int,
        musicVolume: Int
    )

    /**
     * 删除一个片段
     */
    external override fun removeAVFile(index: Int)
}