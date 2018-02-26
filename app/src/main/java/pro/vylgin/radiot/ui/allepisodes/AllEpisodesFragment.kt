package pro.vylgin.radiot.ui.lastentries

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_all_episodes.*
import kotlinx.android.synthetic.main.layout_base_list.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.extension.color
import pro.vylgin.radiot.presentation.global.view.BaseFragment
import pro.vylgin.radiot.presentation.lastentries.AllEpisodesPresenter
import pro.vylgin.radiot.presentation.lastentries.AllEpisodesView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.allepisodes.AllEpisodesAdapter
import pro.vylgin.radiot.ui.allepisodes.sort.SortType
import toothpick.Toothpick


class AllEpisodesFragment : BaseFragment(), AllEpisodesView {

    override val layoutRes = R.layout.fragment_all_episodes

    @InjectPresenter lateinit var presenter: AllEpisodesPresenter

    private val adapter = AllEpisodesAdapter {
        presenter.onEpisodeClicked(it)
    }

    @ProvidePresenter
    fun providePresenter(): AllEpisodesPresenter {
        val scopeName = "all episodes scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        return scope.getInstance(AllEpisodesPresenter::class.java).also {
            Toothpick.closeScope(scopeName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = resources.color(R.color.colorPrimaryDark)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        initSortSpinner()
        initRecyclerView()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { presenter.onMenuClick() }
        toolbar.inflateMenu(R.menu.menu_all_episodes)

        val searchMenuItem = toolbar.menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_all_episodes_item)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                presenter.search(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        searchView.setOnSearchClickListener {  }
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                presenter.pressStartSearchButton()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                presenter.pressStopSearchButton()
                return true
            }

        })
        searchView.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun initSortSpinner() {
        val sorts = listOf(SortType.DESC, SortType.ASC)

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, sorts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                when(sorts[i]) {
                    SortType.ASC -> presenter.onAscPressed()
                    SortType.DESC -> presenter.onDescPressed()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }

    override fun showSortSpinner(show: Boolean) {
        if (show) {
            sortSpinner.visibility = View.VISIBLE
        } else {
            sortSpinner.visibility = View.GONE
        }
    }

    override fun enableRefreshLayout(enable: Boolean) {
        swipeToRefresh.isEnabled = enable
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.adapter = adapter

        swipeToRefresh.setOnRefreshListener { presenter.swipeToRefresh() }
    }

    override fun showEpisodes(episodeNumbers: List<Int>) {
        adapter.initData(episodeNumbers)
    }

    override fun showRefreshProgress(show: Boolean) {
        swipeToRefresh.post { swipeToRefresh?.isRefreshing = show }
    }

    override fun showMessage(message: String) {
        showSnackMessage(message)
    }

    override fun onBackPressed() = presenter.onBackPressed()
}