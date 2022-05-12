package sensors_in_paradise.sonar.util.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.VideoView
import com.google.android.material.slider.RangeSlider
import sensors_in_paradise.sonar.GlobalValues
import sensors_in_paradise.sonar.R
import sensors_in_paradise.sonar.screen_recording.labels_editor.VisualSequenceViewHolder
import java.io.File
import java.util.*

class VideoDialog(
    context: Context,
    videoFile: File,
    title: String? = null,
    message: String? = null,
    onPositiveButtonClickListener: DialogInterface.OnClickListener? = null,
    neutralButtonText: String = "Neutral",
    onNeutralButtonClickListener: DialogInterface.OnClickListener? = null
) : RangeSlider.OnSliderTouchListener {
    private val timer: Timer = Timer()
    private var mediaPlayer: MediaPlayer? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private lateinit var rangeSlider: RangeSlider
    private val updateSeekBarTask = object : TimerTask() {
        override fun run() {
            uiHandler.post {
                val videoTime = mediaPlayer?.timestamp?.anchorMediaTimeUs
                if (videoTime != null) {
                    val progress = videoTime / 1000L
                    rangeSlider.values = rangeSlider.values.apply {
                        this[0] = progress.toFloat()
                    }
                    rangeSlider.invalidate()
                }
            }
        }
    }
    init {
        val builder = AlertDialog.Builder(context)
        val root = LayoutInflater.from(context).inflate(R.layout.video_dialog, null)
        rangeSlider = root.findViewById(R.id.rangeSlider_videoControl_videoDialog)
        rangeSlider.setLabelFormatter { value -> GlobalValues.getDurationAsString(value.toLong()) }
        rangeSlider.isEnabled = false
        rangeSlider.addOnSliderTouchListener(this)
        val endTimeTV = root.findViewById<TextView>(R.id.textView_endTime_videoDialog)
        val videoView = root.findViewById<VideoView>(R.id.videoView_recordingVideo_videoDialog).apply {
            setVideoPath(videoFile.absolutePath)
            setOnPreparedListener { mp ->
                mp.isLooping = true
                mediaPlayer = mp
                rangeSlider.isEnabled = true
                rangeSlider.values = arrayListOf(0f)
                rangeSlider.valueTo = mp.duration.toFloat()
                endTimeTV.text = GlobalValues.getDurationAsString(mp.duration.toLong())
                timer.scheduleAtFixedRate(updateSeekBarTask, 0L, 1000L / VisualSequenceViewHolder.FPS)
            }
            start()
        }

        rangeSlider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                mediaPlayer?.seekTo(value.toInt())
            }
        }

        builder.setView(root)
        if (title != null) {
            builder.setTitle(title)
        }
        if (message != null) {
            builder.setMessage(message)
        }
        if (onNeutralButtonClickListener != null) {
            builder.setNeutralButton(neutralButtonText, onNeutralButtonClickListener)
        }

        if (onPositiveButtonClickListener != null) {
            builder.setPositiveButton(
                "Yes",
                onPositiveButtonClickListener
            )
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        } else {
            builder.setPositiveButton(
                "Ok", null
            )
        }
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    @SuppressLint("RestrictedApi")
    override fun onStartTrackingTouch(slider: RangeSlider) {
        mediaPlayer?.pause()
    }

    @SuppressLint("RestrictedApi")
    override fun onStopTrackingTouch(slider: RangeSlider) {
        mediaPlayer?.start()
    }
}
