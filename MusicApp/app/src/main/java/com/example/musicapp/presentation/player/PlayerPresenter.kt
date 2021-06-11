package com.example.musicapp.presentation.player

import android.util.Log
import com.example.musicapp.data.FirebaseStorage
import com.example.musicapp.presentation.main.MainView
import com.google.firebase.storage.StorageReference
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class PlayerPresenter: MvpPresenter<PlayerView>() {

    private val fbStorage = FirebaseStorage()
    lateinit var songName: StorageReference

    override fun onFirstViewAttach() {
        getSong()
    }

    fun getSong() {
        viewState.showProgress()
        fbStorage.getSimilar(songName.path.removeSuffix(songName.name)).addOnSuccessListener {
            it.items.forEach { sf ->
                Log.w("Name", sf.path)
            }
            viewState.loadItems(it.items.filter { s -> s.name != songName.name  }.shuffled())
            songName.downloadUrl.addOnSuccessListener { uri ->
                viewState.initPlayer(uri.toString())
            }
        }
    }
}
