package com.example.mitmusicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mitmusicplayer.databinding.ActivityPlaylistsBinding
import com.example.mitmusicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistsBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //To unnesesery binding for memory efficiency
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(11)
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistsActivity, 2 )
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter

        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistsActivity)
                .inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
                .setTitle("Playlist Details")
                .setPositiveButton("ADD") {dialog, _ ->
                    val playlistName = binder.playlistName.text
                    val createdBy = binder.yourName.text
                    if (playlistName != null && createdBy != null)
                        if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                            addPlaylist(playlistName.toString(), createdBy.toString())
                        }
                    dialog.dismiss()
                }.show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExist = false
        for (i in musicPlaylist.ref) {
            if (name.equals(i.name)) {
                playlistExist = true
                break
            }
        }
        if (playlistExist) Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }
}