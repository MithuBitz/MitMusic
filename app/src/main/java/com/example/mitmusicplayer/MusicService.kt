package com.example.mitmusicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? =  null
    private lateinit var mediaSesson : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSesson = MediaSessionCompat(baseContext, "Mit Music")
        return myBinder
    }

    inner class MyBinder:Binder() {
        fun currentService() : MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn : Int, playbackSpeed: Float) {

        //Perform action when user click on notification
        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        //intialize proper intent action for the notification button action
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREV)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 1, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.unknown)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_ic)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSesson.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_ic, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.next_ic, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_ic, "Exit", exitPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Set up the duration for the seekbar
            mediaSesson.setMetadata(MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                    .build())
            //Setup the seekbar to move
            mediaSesson.setPlaybackState(PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build())
        }

        startForeground(5, notification)
    }

     fun createMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_ic)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_ic, 1F)
            PlayerActivity.binding.tvSeekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekbarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            //setting the seekbar time
            PlayerActivity.binding.seekbarPA.progress = 0
            PlayerActivity.binding.seekbarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
        } catch (e: Exception) {
            return
        }
    }

    // Setup the seekbar current time position
    fun  seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.tvSeekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekbarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    //if inturrept like call or msg in audio service the song will be pause until and unless the call is end or msg tone is play completely
    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //Pause music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.play_ic)
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_ic)
            showNotification(R.drawable.pause_ic, 0F)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
        } else {
            //Play music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_ic)
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_ic)
            showNotification(R.drawable.pause_ic, 1F)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
        }
    }
}