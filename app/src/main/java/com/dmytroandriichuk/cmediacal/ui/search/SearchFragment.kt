package com.dmytroandriichuk.cmediacal.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.dialog.OfflineDialog
import com.dmytroandriichuk.cmediacal.ui.search.dialog.FilterListDialog
import com.google.firebase.auth.FirebaseAuth

class SearchFragment : Fragment() {

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
//        mToolbar.setNavigationOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            (activity as LandingActivity).finish()
//        }

//        val textView: TextView = root.findViewById(R.id.text_home)
//        searchViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
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
            val dialog = FilterListDialog()
            val manager: FragmentManager = (activity as LandingActivity).supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            dialog.show(transaction, "filter dialog")
            true
        }

        android.R.id.home -> {
            // User chose the "Log Out" action
            Log.i("SearchFragment", "onOptionsItemSelected: Home")
            FirebaseAuth.getInstance().signOut()
            (activity as LandingActivity).finish()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}