package com.production.monoprix.ui.video

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.util.MyMediaController
import kotlinx.android.synthetic.main.activity_full_video.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.io.IOException


 class FullvideoActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback,
    MediaPlayer.OnPreparedListener, MyMediaController.MediaPlayerControl {

     var videoVolume : Int = 6

    var player: MediaPlayer? = null
    var controller: MyMediaController? = null
    lateinit var audio: AudioManager

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_video)
        init()
    }

    fun init() {
        videoVolume = intent.getIntExtra("videoVolume",0)
        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,videoVolume,0)
        /*title*/
//        txt_title.text = getString(R.string.itworks)
        img_left_arrow.setOnClickListener(this)
        setVideoPlayer(intent.getStringExtra("PATH").toString())

    }


    override fun onDestroy() {
        player!!.stop()
        player = null
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,videoVolume,0)
        super.onDestroy()

    }

    @SuppressLint("NewApi")
    private fun setVideoPlayer(str : String) {
        val path =""+str
//        videoView?.setVideoURI(Uri.parse(path))


/////////

        val videoHolder = videoSurface.holder
        videoHolder.addCallback(this)

        player = MediaPlayer()
        controller = MyMediaController(this)
        val videoController = MediaController(this)

        try {
            player?.setAudioStreamType(AudioManager.STREAM_MUSIC)//AudioManager.STREAM_MUSIC
            player?.setDataSource(this, Uri.parse(path))
            player?.setOnPreparedListener(this)

            val am =
                getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )
//            player?.setAudioAttributes(AudioManager.ADJUST_RAISE)
//            AudioManager udio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//            player?.setVolume(10f)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }

        }

    }


    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        player?.setDisplay(p0);
        player?.prepareAsync();
    }

    override fun onPrepared(mp: MediaPlayer?) {
        pb.visibility =View.GONE
        controller?.setMediaPlayer(this);
        controller?.setAnchorView(findViewById<FrameLayout>(R.id.videoSurfaceContainer));
        player?.start();
    }

    override fun start() {
        if (player != null) {
            return player?.start()!!;
        }
    }

    override fun pause() {
        if (player != null) {
            return player?.pause()!!;
        }
    }

    override fun getDuration(): Int {
        if (player != null) {
            return player?.duration!!;
        } else {
            return 0
        }
    }

    override fun getCurrentPosition(): Int {
        if (player != null) {
            return player?.getCurrentPosition()!!;
        } else {
            return 0
        }
    }

    override fun seekTo(pos: Int) {
        if (player != null) {
            return player?.seekTo(pos)!!;
        }
    }

    override fun isPlaying(): Boolean {
        if (player != null) {
            return player?.isPlaying()!!;
        } else {
            return false
        }
    }

    override fun getBufferPercentage(): Int {
        return 0;
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun isFullScreen(): Boolean {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            return true
        } else {
            return false
        }
    }

    override fun isMute(): Boolean {
        val musicVolume: Int = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (musicVolume == 0)
    }


    override fun toggleFullScreen() {
       /* if (isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
//        Toast.makeText(applicationContext,"Toggle Full", Toast.LENGTH_SHORT).show()
        controller?.updateFullScreen()*/
        finish()
    }

    override fun toggleMute() {
        if (isMute()) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,videoVolume,0)
        } else {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,0,0)
        }
        controller?.updateMuteBotton()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        controller?.show()

        return false
    }


}