package com.boww.crypto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boww.crypto.databinding.FragmentListBinding

private const val ARG_REQUEST_STRING = "request_string"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var requestString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requestString = it.getString(ARG_REQUEST_STRING).toString()
        }
    }

    private lateinit var binding: FragmentListBinding
    private val model: HourPriceViewModel by viewModels()
    private val searchItems = mutableListOf<HourPriceViewModel.HourPriceItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        val searchListAdapter = HourPriceAdapter(requireContext(), searchItems)
        val searchListManager = LinearLayoutManager(requireContext())

        binding.hourPriceItemList.apply {
            setHasFixedSize(true)
            adapter = searchListAdapter
            layoutManager = searchListManager
        }

        model.getSearch().observe(viewLifecycleOwner, { listSnapshot ->
            Log.i(TAG, "New search items from view model")
            searchItems.clear()
            searchItems.addAll(listSnapshot)
            searchListAdapter.notifyDataSetChanged()
        })

        model.refresh(requestString)

        return binding.root
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param requestString .
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(requestString: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REQUEST_STRING, requestString)
                }
            }

        private const val TAG = "ListFragment"
    }
}