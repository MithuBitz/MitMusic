package com.example.mitmusicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mitmusicplayer.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {

    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.unknown).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.pause_ic)
            else binding.playPauseBtnNP.setIconResource(R.drawable.play_ic)
        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_ic)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_ic)
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_ic)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_ic)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_ic)
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.play_ic)
        PlayerActivity.isPlaying = false
    }

}