package com.dmytroandriichuk.cmediacal.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.dialog.FilterListDialog
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SearchFragment : Fragment(), FilterListDialog.FilterListDialogListener {

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        setHasOptionsMenu(true)
        val mToolbar = root.findViewById<Toolbar>(R.id.toolbar)
        (activity as LandingActivity).setSupportActionBar(mToolbar)
        mToolbar.setNavigationIcon(R.drawable.ic_log_out)

        val recyclerView = root.findViewById<RecyclerView>(R.id.searchListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val exampleList = ArrayList<SearchListItem>()
        exampleList.add(SearchListItem("aaaaa",totalPrice = 111))
        exampleList.add(SearchListItem("bbbbb",totalPrice = 22))
        exampleList.add(SearchListItem("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        exampleList.add(SearchListItem("a1a2",totalPrice = 300))
        exampleList.add(SearchListItem("1111", "aaaaa aaaaa"))
        exampleList.add(SearchListItem("1111", "aaaaa aaaaa"))
        exampleList.add(SearchListItem("1111", "aaaaa aaaaa"))
        exampleList.add(SearchListItem("1111", "aaaaa aaaaa"))
        exampleList.add(SearchListItem("1111", "aaaaa aaaaa"))

        recyclerView.adapter = SearchListAdapter(exampleList, recyclerView.context)

        val searchView: SearchView = root.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                (recyclerView.adapter as SearchListAdapter).filter.filter(newText)
                return false
            }
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_action_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    // menu buttons action recognition
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.filter_list -> {
            // User chose the "Settings" item, show the app settings UI...
            Log.i("SearchFragment", "onOptionsItemSelected: filter dialog")
            val dialog = FilterListDialog(this)
            val manager: FragmentManager = (activity as LandingActivity).supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            dialog.show(transaction, "filter dialog")
            true
        }

        android.R.id.home -> {
            // User chose the "Log Out" action
            Log.i("SearchFragment", "onOptionsItemSelected: Home")
            FirebaseAuth.getInstance().signOut()
            GoogleSignIn.getClient(
                (activity as LandingActivity), GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN
                ).build()
            )
                .signOut()
            (activity as LandingActivity).finish()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val TAG: String = SearchFragment::class.java.name
    }

    override fun startFiltering() {
        Log.i(TAG, "startFiltering: ")
    }

    override fun setFilter(filter: String) {
        Log.i(TAG, "setFilter: $filter")
    }

    override fun removeFilter(filter: String) {
        Log.i(TAG, "removeFilter: $filter")
    }
}
