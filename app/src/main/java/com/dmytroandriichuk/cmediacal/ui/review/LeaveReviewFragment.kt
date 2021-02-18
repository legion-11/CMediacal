package com.dmytroandriichuk.cmediacal.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.R

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
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        leaveReviewViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}