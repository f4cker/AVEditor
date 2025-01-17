package com.devyk.aveditor.video.camera

import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import com.devyk.aveditor.config.CameraConfiguration
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.video.camera.exception.CameraHardwareException
import com.devyk.aveditor.video.camera.exception.CameraNotSupportException
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

/**
 * <pre>
 *     author  : devyk on 2020-05-29 15:04
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CameraHolder
 * </pre>
 */
public class CameraHolder {
    private val TAG = "CameraHolder"
    private val FOCUS_WIDTH = 80
    private val FOCUS_HEIGHT = 80

    private var mCameraDatas: MutableList<CameraData>? = null
    private var mCameraDevice: Camera? = null


    var cameraData: CameraData? = null
        private set
    var state: State? = null
        private set
    private var mTexture: SurfaceTexture? = null
    private var isTouchMode = false
    private var isOpenBackFirst = true
    private var mConfiguration = CameraConfiguration.createDefault()

    val numberOfCameras: Int
        get() = Camera.getNumberOfCameras()

    val isLandscape: Boolean
        get() = mConfiguration.orientation !== CameraConfiguration.Orientation.PORTRAIT

    enum class State {
        INIT,
        OPENED,
        PREVIEW
    }

    init {
        state = State.INIT
    }

    /**
     * 打开相机
     */
    @Synchronized
    @Throws(CameraHardwareException::class, CameraNotSupportException::class)
    fun openCamera(): Camera {
        if (mCameraDatas == null || mCameraDatas!!.size == 0) {
            mCameraDatas = CameraUtils.getAllCamerasData(isOpenBackFirst)
        }
        val cameraData = mCameraDatas!![0]
        if (mCameraDevice != null && this.cameraData === cameraData) {
            return mCameraDevice!!
        }
        if (mCameraDevice != null) {
            releaseCamera()
        }
        try {
            Log.d(TAG, "open camera " + cameraData.cameraID)
            mCameraDevice = Camera.open(cameraData.cameraID)
        } catch (e: RuntimeException) {
            Log.e(TAG, "fail to connect Camera")
            throw CameraHardwareException(e)
        }

        if (mCameraDevice == null) {
            throw CameraNotSupportException()
        }
        try {
            CameraUtils.initCameraParams(mCameraDevice, cameraData, isTouchMode, mConfiguration)
        } catch (e: Exception) {
            e.printStackTrace()
            mCameraDevice!!.release()
            mCameraDevice = null
            throw CameraNotSupportException()
        }

        this.cameraData = cameraData
        state = State.OPENED
        return mCameraDevice!!
    }


    /**
     *设置纹理
     */
    fun setSurfaceTexture(textureId: Int, listener: SurfaceTexture.OnFrameAvailableListener?) {
        mTexture = SurfaceTexture(textureId)
//        if (state == State.PREVIEW && mCameraDevice != null && mTexture != null) {
        try {
            mCameraDevice?.run {
                setPreviewTexture(mTexture)
                mTexture?.setOnFrameAvailableListener(listener)
            }
        } catch (e: IOException) {
            releaseCamera()
        }

//        }
    }

    fun setSurfaceTexture(
        texture: SurfaceTexture,
        listener: SurfaceTexture.OnFrameAvailableListener?
    ) {
        mTexture = texture
//        if (state == State.PREVIEW && mCameraDevice != null && mTexture != null) {
        try {
            mCameraDevice?.run {
                setPreviewTexture(mTexture)
                mTexture?.setOnFrameAvailableListener(listener)
            }
        } catch (e: IOException) {
            releaseCamera()
        }

//        }
    }

    /**
     * 设置预览相机属性
     */
    fun setConfiguration(configuration: CameraConfiguration) {
        isTouchMode = configuration.focusMode !== CameraConfiguration.FocusMode.AUTO
        isOpenBackFirst = configuration.facing !== CameraConfiguration.Facing.FRONT
        mConfiguration = configuration
    }

    /**
     * 开始预览
     */
    @Synchronized
    fun startPreview() {
        if (state != State.OPENED) {
            return
        }
        if (mCameraDevice == null) {
            return
        }
        if (mTexture == null) {
            return
        }
        try {
            mCameraDevice!!.setPreviewTexture(mTexture)
            mCameraDevice!!.startPreview()
            state = State.PREVIEW
        } catch (e: Exception) {
            releaseCamera()
            e.printStackTrace()
        }

    }

    /**
     * 停止预览
     */
    @Synchronized
    fun stopPreview() {
        if (state != State.PREVIEW) {
            return
        }
        if (mCameraDevice == null) {
            return
        }
        mCameraDevice?.let { cameraDevice ->
            cameraDevice.setPreviewCallback(null)
            try {
                cameraDevice.parameters?.let { parameters ->
                    if (parameters.flashMode != null
                        && parameters.flashMode != Camera.Parameters.FLASH_MODE_OFF
                    ) {
                        parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    }
                    cameraDevice.parameters = parameters
                }
            } catch (error: java.lang.Exception) {
                LogHelper.e(TAG, error.message)
            }
            mCameraDevice!!.stopPreview()
            state = State.OPENED
        }

    }

    /**
     * 释放相机
     */
    @Synchronized
    fun releaseCamera() {
        if (state == State.PREVIEW) {
            stopPreview()
        }
        if (state != State.OPENED) {
            return
        }
        if (mCameraDevice == null) {
            return
        }
        mCameraDevice?.release()
        mCameraDevice = null
        cameraData = null
        state = State.INIT
        CameraUtils.stop()

    }

    fun release() {
        mTexture?.release()
        mTexture = null
        mCameraDatas = null
        isTouchMode = false
        isOpenBackFirst = false
        mConfiguration = CameraConfiguration.createDefault()
    }


    fun setFocusPoint(x: Int, y: Int) {
        if (state != State.PREVIEW || mCameraDevice == null) {
            return
        }
        if (x < -1000 || x > 1000 || y < -1000 || y > 1000) {
            Log.w(TAG, "setFocusPoint: values are not ideal x= $x y= $y")
            return
        }

        val params = mCameraDevice!!.parameters

        if (params != null && params.maxNumFocusAreas > 0) {
            val focusArea = ArrayList<Camera.Area>()
            focusArea.add(Camera.Area(Rect(x, y, x + FOCUS_WIDTH, y + FOCUS_HEIGHT), 1000))

            params.focusAreas = focusArea

            try {
                mCameraDevice!!.parameters = params
            } catch (e: Exception) {
                // Ignore, we might be setting it too
                // fast since previous attempt
            }
        } else {
            Log.w(TAG, "Not support Touch focus mode")
        }
    }

    fun doAutofocus(focusCallback: Camera.AutoFocusCallback): Boolean {
        if (state != State.PREVIEW || mCameraDevice == null) {
            return false
        }
        // Make sure our auto settings aren't locked
        val params = mCameraDevice!!.parameters
        if (params.isAutoExposureLockSupported) {
            params.autoExposureLock = false
        }

        if (params.isAutoWhiteBalanceLockSupported) {
            params.autoWhiteBalanceLock = false
        }

        mCameraDevice!!.parameters = params
        mCameraDevice!!.cancelAutoFocus()
        mCameraDevice!!.autoFocus(focusCallback)
        return true
    }

    fun changeFocusMode(touchMode: Boolean) {
        if (state != State.PREVIEW || mCameraDevice == null || cameraData == null) {
            return
        }
        isTouchMode = touchMode
        cameraData!!.touchFocusMode = touchMode
        if (touchMode) {
            CameraUtils.setTouchFocusMode(mCameraDevice)
        } else {
            CameraUtils.setAutoFocusMode(mCameraDevice)
        }
    }

    fun switchFocusMode() {
        changeFocusMode(!isTouchMode)
    }

    fun cameraZoom(isBig: Boolean): Float {
        if (state != State.PREVIEW || mCameraDevice == null || cameraData == null) {
            return -1f
        }
        val params = mCameraDevice!!.parameters
        if (isBig) {
            params.zoom = min(params.zoom + 1, params.maxZoom)
        } else {
            params.zoom = max(params.zoom - 1, 0)
        }
        mCameraDevice!!.parameters = params
        return params.zoom.toFloat() / params.maxZoom
    }

    fun switchCamera(): Boolean {
        if (state != State.PREVIEW) {
            return false
        }
        try {
            val camera = mCameraDatas!!.removeAt(1)
            mCameraDatas!!.add(0, camera)
            openCamera()
            startPreview()
            return true
        } catch (e: Exception) {
            val camera = mCameraDatas!!.removeAt(1)
            mCameraDatas!!.add(0, camera)
            try {
                openCamera()
                startPreview()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            e.printStackTrace()
            return false
        }
    }

    fun switchLight(): Boolean {
        if (state != State.PREVIEW || mCameraDevice == null || cameraData == null) {
            return false
        }
        if (!cameraData!!.hasLight) {
            return false
        }
        val cameraParameters = mCameraDevice!!.parameters
        if (cameraParameters.flashMode == Camera.Parameters.FLASH_MODE_OFF) {
            cameraParameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        } else {
            cameraParameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
        }
        return try {
            mCameraDevice!!.parameters = cameraParameters
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 设置预览回调，用于软编
     *
     * @param previewCallback
     */
    fun setPreviewCallback(previewCallback: Camera.PreviewCallback) {
        CameraUtils.setPreviewCallback(previewCallback)
    }

    fun updateTexImage() {
        mTexture?.updateTexImage()
    }

    /**
     * 是否后摄像头打开
     */
    public fun isOpenBackFirst(): Boolean {
        return isOpenBackFirst
    }

    /**
     * Is flash light on
     *
     * @return
     */
    fun isFlashLightOn(): Boolean {
        if (state != State.PREVIEW || mCameraDevice == null || cameraData == null) {
            return false
        }
        if (!cameraData!!.hasLight) {
            return false
        }
        val cameraParameters = mCameraDevice!!.parameters
        return cameraParameters.flashMode != Camera.Parameters.FLASH_MODE_OFF
    }


    /**
     * Is back camera
     *
     * @return
     */
    fun isBackCamera(): Boolean {
        if (state != State.PREVIEW || mCameraDevice == null || cameraData == null) {
            return isOpenBackFirst
        }
        return when (cameraData!!.cameraFacing) {
            CameraData.FACING_BACK -> true
            else -> false
        }
    }

    companion object {
        const val TAG = "CameraHolder"
        const val FOCUS_WIDTH = 80
        const val FOCUS_HEIGHT = 80

        private var sHolder: CameraHolder? = null

        @Synchronized
        fun instance(): CameraHolder {
            if (sHolder == null) {
                sHolder = CameraHolder()
            }
            return sHolder as CameraHolder
        }
    }

}
