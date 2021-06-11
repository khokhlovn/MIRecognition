package com.example.musicapp.presentation.main

import com.example.musicapp.data.FirebaseStorage
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class MainPresenter: MvpPresenter<MainView>() {

    private val fbStorage = FirebaseStorage()

    override fun onFirstViewAttach() {
        getSongs("")
    }

    fun getSongs(s: String) {
        viewState.showProgress()
        fbStorage.getAll().addOnSuccessListener {
            viewState.loadItems(it.items.filter { file ->
                file.name.endsWith(".wav") && file.name.contains(s, ignoreCase = true)
            })
            it.prefixes.forEach { sf ->
                sf.listAll().addOnSuccessListener { res ->
                    viewState.addNewItems(res.items.filter { file ->
                        file.name.endsWith(".wav") && file.name.contains(s, ignoreCase = true)
                    })
                    res.prefixes.forEach { sf ->
                        sf.listAll().addOnSuccessListener { res ->
                            viewState.addNewItems(res.items.filter { file ->
                                file.name.endsWith(".wav") && file.name.contains(s, ignoreCase = true)
                            })
                            viewState.hideProgress()
                        }
                    }
                }
            }
        }
    }
}