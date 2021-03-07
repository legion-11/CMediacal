package com.dmytroandriichuk.cmediacal.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.CMedicalApplication
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.ui.search.dialog.FilterListDialog
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

//first fragment of landing screen, provides search for clinics depending on search options
class SearchFragment : Fragment(), FilterListDialog.FilterListDialogListener, SearchListParentAdapter.BookmarksListener {

    private val searchViewModel: SearchViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity as LandingActivity,
                SearchViewModel.SearchViewModelFactory((activity.application as CMedicalApplication).repository))
                .get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        //allows fragment have it's own toolbar
        setHasOptionsMenu(true)
        val mToolbar = root.findViewById<Toolbar>(R.id.toolbar)
        (activity as LandingActivity).setSupportActionBar(mToolbar)
        //replace toolbar back button icon with our icon
        mToolbar.setNavigationIcon(R.drawable.ic_log_out)

        val recyclerView = root.findViewById<RecyclerView>(R.id.searchListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.adapter = SearchListParentAdapter(ArrayList(), this)
        searchViewModel.searchItems.observe(viewLifecycleOwner, {
            (recyclerView.adapter as SearchListParentAdapter).changeDataSet(it)
        })

//        /**
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isViewShown = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when {
                    dy > 0 && isViewShown -> {
                        isViewShown = false
                        mToolbar.animate().translationY(-mToolbar.height.toFloat()).duration = 200
//                        mToolbar.visibility = View.GONE
                    }
                    dy < 0 && !isViewShown ->{
                        isViewShown = true
                        mToolbar.animate().translationX(0f).translationY(0f).duration = 200
//                        mToolbar.visibility = View.VISIBLE
                    }
                }
            }
        })
//        */

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

        // get all from firebase db
        if (searchViewModel.filters.isEmpty() && searchViewModel.provinces.isEmpty()){
            startQuery()
        }

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
            Log.d("SearchFragment", "onOptionsItemSelected: filter dialog")
            val dialog = FilterListDialog(this)

            val manager: FragmentManager = (activity as LandingActivity).supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            dialog.show(transaction, "filter dialog")
            true
        }

        android.R.id.home -> {
            // User chose the "Log Out" action
            Log.d("SearchFragment", "onOptionsItemSelected: Home")
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
        const val TAG: String = "SearchFragment"
    }

    override val filters: ArrayList<String> get() = searchViewModel.filters
    override val provinces: ArrayList<String> get() = searchViewModel.provinces

    //start filtering list by tags
    override fun startQuery() {
        Log.d(TAG, "startQuery: ")
        searchViewModel.searchQuery()
    }

    //add tag for filtering
    override fun setFilter(filter: String) {
        Log.d(TAG, "setFilter: $filter")
        searchViewModel.filters.add(filter)
        Log.d(TAG, "setFilter: ${searchViewModel.filters}")
    }

    //remove filtering tag
    override fun removeFilter(filter: String) {
        Log.d(TAG, "removeFilter: $filter")
        searchViewModel.filters.remove(filter)
        Log.d(TAG, "removeFilter: ${searchViewModel.filters}")
    }

    //add province tag
    override fun setProvince(filter: String) {
        Log.d(TAG, "setProvince: $filter")
        searchViewModel.provinces.add(filter)
        Log.d(TAG, "setProvince: ${searchViewModel.provinces}")
    }

    //remove province tag
    override fun removeProvince(filter: String) {
        Log.d(TAG, "removeProvince: $filter")
        searchViewModel.provinces.remove(filter)
        Log.d(TAG, "removeProvince: ${searchViewModel.provinces}")
    }

    override fun addBookmark(item: SearchListParentItem) {
        Log.d(TAG, "addBookmark: $item")
        val clinic = Clinic(
            item.id,
            searchViewModel.getUser(),
            item.name,
            item.address,
            item.lat,
            item.lng,
        )
        searchViewModel.insert(clinic)
        for (servicePrice in item.servicesPrices) {
            searchViewModel.insert(ServicePrice(0, servicePrice.key, servicePrice.value, clinic.crossRefId))
        }
    }

    override fun removeBookmark(item: SearchListParentItem) {
        Log.d(TAG, "removeBookmark: $item")
        val clinic = Clinic(
            item.id,
            searchViewModel.getUser(),
            item.name,
            item.address,
            item.lat,
            item.lng,
        )
        searchViewModel.delete(clinic)
    }
}
