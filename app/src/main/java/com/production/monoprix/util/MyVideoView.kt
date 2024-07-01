package com.production.monoprix.util

import android.annotation.SuppressLint
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

class MyVideoView : VideoView {
    var mMediaController: MyMediaController? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @SuppressLint("NewApi")
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    fun setCustomMediaController(controller: MyMediaController?) {
        // super.setMediaController(controller);
        if (mMediaController != null) {
            mMediaController!!.hide()
        }
        mMediaController = controller
        attachMediaController()
    }

    private fun attachMediaController() {
        /*
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View)this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }*/
    }
}