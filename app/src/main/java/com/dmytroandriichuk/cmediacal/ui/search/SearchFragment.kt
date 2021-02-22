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
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

//first frgment of landing screen, provides search for clinics depending on search options
class SearchFragment : Fragment(), FilterListDialog.FilterListDialogListener {

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        //allows each fragment have it's own toolbar
        setHasOptionsMenu(true)
        val mToolbar = root.findViewById<Toolbar>(R.id.toolbar)
        (activity as LandingActivity).setSupportActionBar(mToolbar)
        //replace toolbar back button icon with our icon
        mToolbar.setNavigationIcon(R.drawable.ic_log_out)

        val recyclerView = root.findViewById<RecyclerView>(R.id.searchListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //TODO replace with db implementation
        val exampleList = ArrayList<SearchListParentItem>()
        exampleList.add(SearchListParentItem("place"))
        exampleList.add(SearchListParentItem("cheap",servicesPrices = hashMapOf("something" to 20f,"something2" to 20f,"something3" to 10f)))
        exampleList.add(SearchListParentItem("really long name aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        exampleList.add(SearchListParentItem())
        exampleList.add(SearchListParentItem("expensive", "aaaaa aaaaa",servicesPrices = hashMapOf("something" to 200f,"something2" to 200f,"something3" to 200f)))
        exampleList.add(SearchListParentItem("palce"))
        exampleList.add(SearchListParentItem("exp"))
        exampleList.add(SearchListParentItem("cheap not"))
        exampleList.add(SearchListParentItem())

        recyclerView.adapter = SearchListParentAdapter(exampleList, recyclerView.context)

        //provide search through recycleView
        val searchView: SearchView = root.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                (recyclerView.adapter as SearchListParentAdapter).filter.filter(newText)
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
            // User chose the "Filter" item, show the app filter dialog to specify search...
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
            //if you do not sign out from google you can not choose other user
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

    //start filtering list by tags
    override fun startFiltering() {
        Log.i(TAG, "startFiltering: ")
    }

    //add tags for filtering
    override fun setFilter(filter: String) {
        Log.i(TAG, "setFilter: $filter")
    }

    //remove filtering tags
    override fun removeFilter(filter: String) {
        Log.i(TAG, "removeFilter: $filter")
    }
}
