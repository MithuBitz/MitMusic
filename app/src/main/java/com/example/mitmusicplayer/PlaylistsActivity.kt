package com.example.mitmusicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mitmusicplayer.databinding.ActivityPlaylistsBinding

class PlaylistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnPLA.setOnClickListener { finish() }
    }
}