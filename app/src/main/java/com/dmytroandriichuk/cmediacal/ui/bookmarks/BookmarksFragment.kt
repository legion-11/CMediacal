package com.dmytroandriichuk.cmediacal.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.CMedicalApplication
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.bookmarks.model.ClinicListItem
import com.google.android.material.snackbar.Snackbar

class BookmarksFragment : Fragment(), ClinicListParentAdapter.BookmarksListener {

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
        recyclerView.adapter = ClinicListParentAdapter(ArrayList(), this)
        bookmarksViewModel.bookmarks.observe(viewLifecycleOwner, { arrayOfClinics ->
            val listItems = ArrayList(arrayOfClinics.map { ClinicListItem(it, bookmarked = true) })
            (recyclerView.adapter as ClinicListParentAdapter).changeDataSet(listItems)
        })
        return root
    }

    override fun addBookmark(item: ClinicListItem) {
        bookmarksViewModel.insert(item.clinic)
    }

    override fun removeBookmark(item: ClinicListItem) {
        bookmarksViewModel.delete(item.clinic)
        this.view?.let {
            Snackbar.make(it, "Bookmark is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    addBookmark(item)
                }.show()
        }
    }
}