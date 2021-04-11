package com.dmytroandriichuk.cmediacal.fragments.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.CMedicalApplication
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.google.android.material.snackbar.Snackbar

// fragment that shows all clinics in our database ordered by date
class BookmarksFragment : Fragment(), BookmarksListParentAdapter.ItemClickListener {

    private val bookmarksViewModel: BookmarksViewModel by viewModels {
        BookmarksViewModel.BookmarksViewModelFactory((activity?.application as CMedicalApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.bookmarksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = BookmarksListParentAdapter(ArrayList(), this, (activity as LandingActivity).placesClient)
        bookmarksViewModel.bookmarks.observe(viewLifecycleOwner, { arrayOfClinics ->
            val listItems = ArrayList(arrayOfClinics.map { ClinicListItem(it, bookmarked = true) })
            (recyclerView.adapter as BookmarksListParentAdapter).changeDataSet(listItems)
        })
        return root
    }

    // return bookmark to db if we deleted it accidentally
    override fun addBookmark(item: ClinicListItem) {
        bookmarksViewModel.insert(item.clinic)
    }

    // remove bookmark from db
    override fun removeBookmark(item: ClinicListItem) {
        bookmarksViewModel.delete(item.clinic)
        this.view?.let {
            Snackbar.make(it, "Bookmark is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    addBookmark(item)
                }.show()
        }
    }

    override fun itemClicked(item: ClinicListItem) {
        (activity as LandingActivity).openDetailsActivity(item)
    }
}