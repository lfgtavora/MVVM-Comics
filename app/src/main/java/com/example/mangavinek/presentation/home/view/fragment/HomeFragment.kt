package com.example.mangavinek.presentation.home.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import com.example.mangavinek.R
import org.jetbrains.anko.AnkoLogger
import android.widget.Toast.makeText as makeText1
import com.example.mangavinek.core.util.PaginationScroll
import com.example.mangavinek.model.home.entity.Model
import com.example.mangavinek.presentation.home.presenter.HomePresenter
import com.example.mangavinek.presentation.home.PresentationHome
import com.example.mangavinek.presentation.home.view.adapter.ItemAdapter
import kotlinx.coroutines.*
import com.example.mangavinek.presentation.home.view.viewmodel.NewsViewModel
import org.jsoup.select.Elements

class HomeFragment : Fragment(), AnkoLogger, PresentationHome.View {

    private val adapterItem: ItemAdapter by lazy{
        ItemAdapter(itemList, context!!)
    }

    private val presenter by lazy{
        presenter()
    }

    private var itemList = arrayListOf<Model>()
    private var releasedLoad: Boolean = true
    private var page : Int = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.apply {
            initViewModel()
            loadData()
        }

        swipe_refresh.setOnRefreshListener{
            GlobalScope.launch(context = Dispatchers.Main) {
                if(releasedLoad){
                    delay(1000)
                    page = 2
                    adapterItem.clear(itemList)
                    presenter.initViewModel()
                }
                swipe_refresh.isRefreshing = false
            }
        }

        initUi()
    }

    override fun viewModel(): NewsViewModel {
        return ViewModelProviders.of(this).get(NewsViewModel::class.java)
    }

    override fun presenter(): PresentationHome.Presenter {
        return HomePresenter(this, activity!!)
    }

    override fun populate(elements: Elements) {
        elements.forEach {
            itemList.add(Model(it))
        }
        adapterItem.notifyItemChanged(itemList.size - 20, itemList.size)
    }

    private fun initUi() {
        with(recycler_view) {
            adapter = adapterItem
            val gridLayoutManager = GridLayoutManager(context, 3)
            addOnScrollListener(object : PaginationScroll(gridLayoutManager) {
                override fun loadMoreItems() {
                    presenter.initViewModel(page++)
                    progress_bottom.visibility = View.VISIBLE
                    releasedLoad = false
                }

                override fun isLoading(): Boolean {
                    return releasedLoad
                }

                override fun hideMoreItems() {
                    progress_bar.visibility = View.GONE
                }
            })
            layoutManager = gridLayoutManager
        }
    }

    override fun screenSuccess(){
        progress_bar.visibility = View.GONE
        progress_bottom.visibility = View.GONE
        text_erro.visibility = View.GONE
        releasedLoad = true
    }

    override fun screenLoading(){
        progress_bar.visibility = View.VISIBLE
        text_erro.visibility = View.GONE
    }

    override fun screenError(){
        text_erro.visibility = View.VISIBLE
        progress_bottom.visibility = View.GONE
        progress_bar.visibility = View.GONE
    }
}
