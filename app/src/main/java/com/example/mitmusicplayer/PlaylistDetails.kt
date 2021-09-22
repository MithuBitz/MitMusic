package com.example.mitmusicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mitmusicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding =  ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int

        try {
            //if some song are deleted from the filemanager than it will handle to remove those songs
            PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
                    checkPlaylist(playlist = PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist)

        } catch (e: Exception) {}

        //Populate the recycler view
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }

        //Remove btn functionality
        binding.removeBtnPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                    .setMessage("Do you want to remove all songs from the playlist?")
                    .setPositiveButton("Yes") {dialog, _ ->
                        PlaylistsActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.clear()
                        adapter.refreshPlaylist()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") {dialog, _ ->
                        dialog.dismiss()
                    }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfo.text = "Total ${adapter.itemCount} Songs\n" +
                "Created On: \n ${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdOn} \n" +
                " -- ${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"

        //if the song in the playlist is more than 1 then set the image from the first song and also visible the shuffle btn
        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.unknown).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()

            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
            //For storeing created playlist
            val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
            editor.putString("MusicPlaylist", jsonStringPlaylist)
            editor.apply()

        }
    }
}