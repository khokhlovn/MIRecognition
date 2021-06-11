package com.example.musicapp.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.presentation.adapter.CustomAdapter
import com.example.musicapp.presentation.player.PlayerFragment
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import moxy.MvpFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter


class MainFragment: MvpFragment(), MainView {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() = MainPresenter()

    private val adapter = CustomAdapter()

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (query.isEmpty())
                        presenter.getSongs("")
                    else
                        presenter.getSongs(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.equals("")){
                    this.onQueryTextSubmit("");
                }
                return true
            }
        })
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun loadItems(list: List<StorageReference>) {
        rv_list.layoutManager = LinearLayoutManager(activity.baseContext)
        rv_list.adapter = adapter
        adapter.listener = { sf -> play(sf)}
        adapter.names.clear()
        adapter.names.addAll(list)
    }

    override fun addNewItems(list: List<StorageReference>) {
        adapter.names.addAll(list)
        rv_list.adapter?.notifyDataSetChanged()
    }

    private fun play(sr: StorageReference) {
        childFragmentManager.beginTransaction().replace(
                R.id.fcv, PlayerFragment().getInstance(sr)
        ).commit()
    }
}