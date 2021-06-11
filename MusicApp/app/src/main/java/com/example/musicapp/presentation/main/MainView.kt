package com.example.musicapp.presentation.main

import com.google.firebase.storage.StorageReference
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun loadItems(list: List<StorageReference>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addNewItems(list: List<StorageReference>)
}
