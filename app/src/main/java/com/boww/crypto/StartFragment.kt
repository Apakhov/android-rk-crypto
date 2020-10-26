package com.boww.crypto

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.boww.crypto.databinding.FragmentStartBinding

/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.bttnToday.setOnClickListener {
            val bundle = bundleOf(
                "fsym" to "BTC",
                "tsym" to "RUB",
                "market" to "CCCAGG",
                "limit" to 24,
                "timestamp" to getCurrentDateTime().ts(),
                "high" to 12130.4f,
                "low" to 12133.4f,
                "open" to 12133.4f,
                "close" to 12100.4f,
            )
            it.findNavController().navigate(R.id.action_startFragment_to_listFragment, bundle)
        }
        binding.bttnYesterday.setOnClickListener {
            val bundle = bundleOf(
                "fsym" to "BTC",
                "tsym" to "RUB",
                "market" to "CCCAGG",
                "limit" to 24,
                "timestamp" to getCurrentDateTime().ts()-24*3600,
                "high" to 12130.4f,
                "low" to 12133.4f,
                "open" to 12133.4f,
                "close" to 12100.4f,
            )
            Log.i("DATE_TAG", (getCurrentDateTime().ts()-24*3600).toString())
            it.findNavController().navigate(R.id.action_startFragment_to_listFragment, bundle)
        }
        binding.bttnYeyesterday.setOnClickListener {
            val bundle = bundleOf(
                "fsym" to "BTC",
                "tsym" to "RUB",
                "market" to "CCCAGG",
                "limit" to 24,
                "timestamp" to getCurrentDateTime().ts()-2*24*3600,
                "high" to 12130.4f,
                "low" to 12133.4f,
                "open" to 12133.4f,
                "close" to 12100.4f,
            )
            Log.i("DATE_TAG", (getCurrentDateTime().ts()-2*24*3600).toString())
            it.findNavController().navigate(R.id.action_startFragment_to_listFragment, bundle)
        }
        return binding.root
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = StartFragment()

        private const val TAG = "StartFragment"
    }
}