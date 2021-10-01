package com.dsv.basemvvm.framework.ui.views

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.dsv.basemvvm.R
import com.dsv.basemvvm.databinding.ViewExoplayerReplayreviewsBinding
import com.dsv.basemvvm.framework.ui.common.Constants
import com.dsv.basemvvm.framework.ui.common.Util
import com.dsv.basemvvm.framework.ui.common.displayMetrics
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ExoPlayerReplayReviewsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) , Player.Listener {

    var exoPlayer: SimpleExoPlayer? = null
    var hasStarted = false
    var onVideoReady: ((Int) -> Unit)? = null
    var onProgressChanged: ((Int) -> Unit)? = null
    var mainHandler: Handler? = null

    private val reportProgressTask = object : Runnable {
        override fun run() {
            if (exoPlayer?.isPlaying == true) {
                onProgressChanged?.invoke((exoPlayer?.currentPosition ?: 0 / 1000).toInt())
            }
            mainHandler?.postDelayed(this, 50)
        }
    }

    private val binding by lazy {
        ViewExoplayerReplayreviewsBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        exoPlayer = SimpleExoPlayer.Builder(context).build()
        binding.exoPlayerView.player = exoPlayer
        binding.controls.player = exoPlayer;
    }

    fun setOnVideoReadyCallback(callback: ((Int) -> Unit)?) {
        this.onVideoReady = callback
    }

    fun setOnProgressChangedCallback(callback: ((Int) -> Unit)?) {
        this.onProgressChanged = callback
    }

    fun initializeMediaPlayer() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.exoPlayerView.visibility = View.GONE

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, "DemoRR")
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            MediaItem.fromUri(Constants.VIDEO_URL))
        binding.exoPlayerView.controllerShowTimeoutMs = 2000
        binding.exoPlayerView.controllerAutoShow = false
        binding.exoPlayerView.setKeepContentOnPlayerReset(true)
        binding.exoPlayerView.keepScreenOn = true
        exoPlayer?.playWhenReady = false
        exoPlayer?.setMediaSource(videoSource)
        exoPlayer?.prepare()
        exoPlayer?.addListener(this)
        adjustVideoSize()
        mainHandler = Handler(Looper.getMainLooper())
        findViewById<TextView>(R.id.testButton).setOnClickListener {
            Toast.makeText(context, "TEST BUTTON", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        if (exoPlayer?.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                onVideoPlay()
            } else if (hasStarted) {
                onVideoPause()
            }
        }
    }

    private fun onVideoPlay() {
        Toast.makeText(context, "On Video Play", Toast.LENGTH_SHORT).show()
        if (!hasStarted) {
            hasStarted = true
            mainHandler?.post(reportProgressTask)
        }
    }

    private fun onVideoPause() {
        Toast.makeText(context, "On Video Pause", Toast.LENGTH_SHORT).show()
    }

    fun onVideoDisengage() {
        hasStarted = false
        mainHandler?.removeCallbacks(reportProgressTask)
        exoPlayer?.let {
            it.playWhenReady = false
            it.stop()
            it.seekTo(0)
        }
    }

    fun seekTo(miliSeconds: Int) {
        exoPlayer?.seekTo(miliSeconds.toLong())
    }

    private fun onVideoStop() {
        Toast.makeText(context, "On Video Stop", Toast.LENGTH_SHORT).show()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == Player.STATE_READY) {
            binding.pbLoading.visibility = View.GONE
            binding.exoPlayerView.visibility = View.VISIBLE
            val duration = (exoPlayer?.duration?:0 / 1000).toInt()
            onVideoReady?.invoke(duration)
            onVideoReady = null
        } else if (playbackState == Player.STATE_ENDED) {
            onVideoStop()
        }
    }

    private fun adjustVideoSize() {
        val width = (context as Activity).displayMetrics().widthPixels
        val params = layoutParams
        params.height = width * 9 / 16 + Util.dpToPx(context, 30)
        layoutParams = params

        val exoLayoutParams = binding.exoPlayerView.layoutParams
        exoLayoutParams.height = width * 9 / 16
        binding.exoPlayerView.layoutParams = exoLayoutParams

        invalidate()
    }

    fun release() {
        exoPlayer?.let {
            it.removeListener(this)
            it.release()
            exoPlayer = null
        }
    }



}