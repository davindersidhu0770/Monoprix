package com.production.monoprix.ui.howitworks

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
import com.production.monoprix.util.SessionManager
import kotlinx.android.synthetic.main.activity_how_it_works.*
import kotlinx.android.synthetic.main.dialog_how_it_works.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.io.IOException


class HowItWorksActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback,
    MediaPlayer.OnPreparedListener, MyMediaController.MediaPlayerControl {

    var filtercustomDialog: Dialog? = null
    var mediaC: MediaController? = null

    var player: MediaPlayer? = null
    var controller: MyMediaController? = null
    lateinit var audio: AudioManager
    lateinit var sessionManager: SessionManager

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_it_works)
        init()
    }

    fun init() {
        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sessionManager = SessionManager(this)
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, sessionManager.volume, 0)
        /*title*/
        txt_title.text = getString(R.string.itworks)
        img_left_arrow.setOnClickListener(this)

        /*dialog box*/
        bottomDialog()
        setVideoPlayer()

    }


    override fun onDestroy() {
        player!!.stop()
        player = null
        sessionManager.volume = sessionManager.volume
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, sessionManager.volume, 0)
        super.onDestroy()

    }

    @SuppressLint("NewApi")
    private fun setVideoPlayer() {
        val path =
            "android.resource://" + packageName + "/" + R.raw.monop_easy_scan_shop
        videoView?.setVideoURI(Uri.parse(path))

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
            R.id.txt_continue -> {
                filtercustomDialog!!.dismiss()
            }

        }

    }

    fun bottomDialog() {
        val inflater = layoutInflater as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_how_it_works, null)
        // Build the dialog
        filtercustomDialog = Dialog(this, R.style.MyDialogTheme)
        filtercustomDialog!!.setContentView(customView)
        // dialog with bottom and with out padding
        val window = filtercustomDialog!!.getWindow()
        val wlp = window!!.getAttributes()
        wlp.gravity = Gravity.BOTTOM
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT
        window!!.attributes = wlp
        //animation
        // filtercustomDialog!!.window!!.attributes.windowAnimations = animationSource //style id
        filtercustomDialog!!.show()
        filtercustomDialog!!.txt_continue.setOnClickListener(this)
        filtercustomDialog!!.setCanceledOnTouchOutside(false)
        filtercustomDialog!!.setCancelable(true)

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
        if (isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
//        Toast.makeText(applicationContext,"Toggle Full", Toast.LENGTH_SHORT).show()
        controller?.updateFullScreen()
    }

    override fun toggleMute() {
        if (isMute()) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, sessionManager.volume, 0)
        } else {
            sessionManager.volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        }
        controller?.updateMuteBotton()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        controller?.show()
        return false
    }


}