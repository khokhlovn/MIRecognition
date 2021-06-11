package com.example.musicapp.presentation.player

import com.google.firebase.storage.StorageReference
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface PlayerView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun initPlayer(s: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun loadItems(list: List<StorageReference>)
}
