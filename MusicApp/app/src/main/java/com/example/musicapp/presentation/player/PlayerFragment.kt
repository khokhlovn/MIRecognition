package com.example.musicapp.presentation.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.presentation.adapter.CustomAdapter
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.recycler_item.*
import moxy.MvpFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.*


class PlayerFragment : MvpFragment(), PlayerView {

    fun getInstance(s: StorageReference): PlayerFragment {
        return PlayerFragment().apply {
            this.songName = s
        }
    }

    @InjectPresenter
    lateinit var presenter: PlayerPresenter

    @ProvidePresenter
    fun providePresenter() = PlayerPresenter()

    private val adapter = CustomAdapter()

    private var isPlaying = false

    val timer = Timer()

    private lateinit var songName: StorageReference
    private var prevSong: StorageReference? = null

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.songName = songName
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun initPlayer(s: String) {
        try {
            tv_name.text = presenter.songName.name.removeSuffix(".wav")
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            adapter.listener = { sf ->
                timer.cancel()
                mediaPlayer.release()
                prevSong = presenter.songName
                presenter.songName = sf
                presenter.getSong()
            }
            mediaPlayer.setDataSource(s)
            mediaPlayer.prepare()
            mediaPlayer.start()
            hideProgress()
            isPlaying = true
            seekBar.max = mediaPlayer.duration
            seekBar.isClickable = false
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    seekBar.progress = mediaPlayer.currentPosition
                }
            }, 0, 1000)
            if (prevSong != null)
                iv_prev.visibility = View.VISIBLE
            iv_next.setOnClickListener {
                timer.cancel()
                mediaPlayer.release()
                prevSong = presenter.songName
                presenter.songName = adapter.names[0]
                presenter.getSong()
            }
            iv_prev.setOnClickListener {
                timer.cancel()
                mediaPlayer.release()
                presenter.songName = prevSong!!
                prevSong = null
                presenter.getSong()
            }
            iv_play.setImageResource(android.R.drawable.ic_media_pause)
            iv_play.setOnClickListener {
                if (isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                    iv_play.setImageResource(android.R.drawable.ic_media_play)
                }

                else {
                    mediaPlayer.start()
                    isPlaying = true
                    iv_play.setImageResource(android.R.drawable.ic_media_pause)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun loadItems(list: List<StorageReference>) {
        rv_list.layoutManager = LinearLayoutManager(activity.baseContext)
        rv_list.adapter = adapter
        adapter.names.clear()
        adapter.names.addAll(list)
    }
}
