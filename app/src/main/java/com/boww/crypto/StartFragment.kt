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
        binding.bttnRun.setOnClickListener {
            val bundle = bundleOf(
                "request_string" to binding.plainTextInput.text.toString(),
            )
            it.findNavController().navigate(R.id.action_startFragment_to_listFragment, bundle)
        }
        uselessButtonsBind()
        return binding.root
    }

    fun uselessButtonsBind() {
        binding.bttnAlarm.setOnClickListener {
            Log.i(TAG, "called alarm")
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "alaaaaaaarm!!!i!@")
                putExtra(AlarmClock.EXTRA_HOUR, 13)
                putExtra(AlarmClock.EXTRA_MINUTES, 37)
            }
            requireActivity().startActivity(intent)
        }
        
        binding.bttnMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:55.77,37.69")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        }
        binding.bttnGoogle.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, "почему андроид хуже ios")
            }
            requireActivity().startActivity(intent)
        }
        binding.bttnLink.setOnClickListener {
            val parsedUri: Uri = Uri.parse("https://newsapi.org/v2/everything?q=putin&from=2020-09-19&sortBy=publishedAt&apiKey=40f43a3237564359b27fe7bf511b6a51")
            val intent = Intent(Intent.ACTION_VIEW, parsedUri)
            requireActivity().startActivity(intent)
        }
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