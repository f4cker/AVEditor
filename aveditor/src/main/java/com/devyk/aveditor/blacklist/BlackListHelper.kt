package com.devyk.aveditor.blacklist

import android.os.Build
import android.text.TextUtils

/**
 * <pre>
 *     author  : devyk on 2020-05-28 23:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BlackListHelper
 * </pre>
 */


object BlackListHelper {
    private val BLACKLISTED_AEC_MODELS = arrayOf("Nexus 5")// Nexus 5

    private val BLACKLISTED_FPS_MODELS = arrayOf("OPPO R9", "Nexus 6P")


    fun deviceInAecBlacklisted(): Boolean {
        val blackListedModels = listOf(*BLACKLISTED_AEC_MODELS)
        for (blackModel in blackListedModels) {
            val model = Build.MODEL
            if (!TextUtils.isEmpty(model) && model.contains(blackModel)) {
                return true
            }
        }
        return false
    }

    fun deviceInFpsBlacklisted(): Boolean {
        val blackListedModels = listOf(*BLACKLISTED_FPS_MODELS)
        for (blackModel in blackListedModels) {
            val model = Build.MODEL
            if (!TextUtils.isEmpty(model) && model.contains(blackModel)) {
                return true
            }
        }
        return false
    }
}