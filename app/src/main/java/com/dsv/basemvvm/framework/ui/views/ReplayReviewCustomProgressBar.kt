package com.dsv.basemvvm.framework.ui.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.dsv.basemvvm.R
import com.dsv.basemvvm.databinding.ViewReplayreviewCustomprogressbarBinding
import com.dsv.basemvvm.domain.models.ReplayComment
import com.dsv.basemvvm.domain.models.TimestampsResponse
import com.dsv.basemvvm.framework.ui.common.Util
import com.dsv.basemvvm.framework.ui.common.displayMetrics
import com.google.android.material.slider.Slider

class ReplayReviewCustomProgressBar  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr)  {

    var replayComments: List<ReplayComment>? = null
    var duration: Float = -1F
    var onCommentClicked: ((ReplayComment?) -> Unit)? = null
    var onSetNewProgress: ((Int) -> Unit)? = null

    private val binding by lazy {
        ViewReplayreviewCustomprogressbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun loadComments(duration: Int, timestampsResponse: TimestampsResponse, onCommentClicked: ((ReplayComment?) -> Unit), onSetNewProgress: ((Int) -> Unit)?) {
        this.replayComments = timestampsResponse.timestamps
        this.duration = duration.toFloat()
        this.onCommentClicked = onCommentClicked
        this.onSetNewProgress = onSetNewProgress
        if (duration <= 0) {
            return
        }
        binding.sliderProgress.valueTo = duration.toFloat()
        binding.sliderProgress.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                onSetNewProgress?.invoke(value.toInt())
            }
        }

        replayComments?.let {
            for (i in it.indices) {
                loadIndividualComment(it[i], i)
            }
        }
    }

    private fun loadIndividualComment(comment: ReplayComment, index: Int) {
        val circleView = ImageView(context)
        circleView.setImageDrawable(ContextCompat.getDrawable(context, if (comment.fromCoach) R.drawable.coach_comment_circle else R.drawable.padawan_comment_circle))
        val newlayoutParams = RelativeLayout.LayoutParams(
            Util.dpToPx(context, 12),
            Util.dpToPx(context, 12)
        )
        newlayoutParams.setMargins(getMarginForComment(comment.timeInSeconds), 0, 0, 0)
        newlayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        circleView.setOnClickListener {
            onCommentClicked?.invoke(replayComments?.get(index))
        }
        binding.commentsLayout.addView(circleView, newlayoutParams)
    }

    fun updateProgress(newProgress: Int) = when {
        newProgress > duration -> binding.sliderProgress.value = duration
        newProgress <= 0 -> binding.sliderProgress.value = 0F
        else -> binding.sliderProgress.value = newProgress.toFloat()
    }

    private fun getMarginForComment(commentTimeInSeconds: Int) : Int {
        val fullWidth = (context as Activity).displayMetrics().widthPixels
        return (commentTimeInSeconds * 1000 * fullWidth / duration.toInt()) - Util.dpToPx(context, 6)
    }
}