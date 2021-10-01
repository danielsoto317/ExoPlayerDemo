package com.dsv.basemvvm.framework.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.dsv.basemvvm.databinding.ActivityMainBinding
import com.dsv.basemvvm.domain.models.ReplayComment
import com.dsv.basemvvm.domain.models.TimestampsResponse
import com.dsv.basemvvm.framework.ui.common.Constants
import com.dsv.basemvvm.framework.ui.common.Util
import com.dsv.basemvvm.framework.ui.common.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    private lateinit var timestampsResponse: TimestampsResponse
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentsAdapter = CommentsAdapter(::onCommentClicked)
        binding.recycler.adapter = commentsAdapter

        viewModel.showMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
        loadTimestampResponse()
        viewModel.onCreate()
    }

    private fun loadTimestampResponse() {
        val json = Util.loadJSONFromAsset(this, Constants.TIMESTAMPS_FILE)
        if (json != null) {
            val gson = Gson()
            timestampsResponse = gson.fromJson(json, TimestampsResponse::class.java)
            commentsAdapter.commentList = timestampsResponse.timestamps
        }
    }

    override fun onStart() {
        super.onStart()
        binding.exoPlayerView.setOnVideoReadyCallback {
            binding.replayReviewCustomProgressBar.loadComments(it, timestampsResponse, ::onCommentClicked, ::onSetNewProgress)
        }
        binding.exoPlayerView.setOnProgressChangedCallback {
            binding.replayReviewCustomProgressBar.updateProgress(it)
        }
        binding.exoPlayerView.initializeMediaPlayer()
    }

    fun onSetNewProgress(value: Int) {
        binding.exoPlayerView.seekTo(value)
    }

    fun onCommentClicked(replayComment: ReplayComment?) {
        replayComment?.let {
            binding.replayReviewCustomProgressBar.updateProgress(it.timeInSeconds * 1000)
            binding.exoPlayerView.seekTo(it.timeInSeconds * 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.exoPlayerView.onVideoDisengage()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.exoPlayerView.release()
    }
}