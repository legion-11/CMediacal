package com.dmytroandriichuk.cmediacal.ui.review

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.google.firebase.auth.FirebaseAuth

class LeaveReviewFragment : Fragment() {

    private lateinit var leaveReviewViewModel: LeaveReviewViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        leaveReviewViewModel =
                ViewModelProvider(this).get(LeaveReviewViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_leave_review, container, false)

        val mToolbar = root.findViewById<Toolbar>(R.id.toolbar2)
        (activity as LandingActivity).setSupportActionBar(mToolbar)
        mToolbar.setNavigationIcon(R.drawable.ic_log_out)
        mToolbar.setNavigationOnClickListener {
            FirebaseAuth.getInstance().signOut()
            (activity as LandingActivity).finish()
        }

//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        leaveReviewViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}