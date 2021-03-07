package com.dmytroandriichuk.cmediacal.ui.bookmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.CMedicalApplication
import com.dmytroandriichuk.cmediacal.R

class BookmarksFragment : Fragment() {

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

        bookmarksViewModel.bookmarks.observe(viewLifecycleOwner, {
            for (i in it) {
                Log.d("TAG", "onCreateView: $i")
            }
        })
        return root
    }
}