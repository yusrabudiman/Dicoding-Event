package com.example.dicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.adapterviewmodel.ReviewVerticalAdapter
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.ui.detail.DetailEventActivity

class UpComingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReviewVerticalAdapter
    private val upComingViewModel: UpComingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReviewVerticalAdapter { eventId ->
            val intent = Intent(context, DetailEventActivity::class.java)
            intent.putExtra("EXTRA_EVENT_ID", eventId)
            context?.startActivity(intent)
        }

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.adapter = adapter

        observeViewModel()
        upComingViewModel.getActiveEvents()

        binding.btnRefresh.setOnClickListener {
            resetErrorMessage()
            upComingViewModel.getActiveEvents()
        }
    }

    private fun resetErrorMessage() {
        upComingViewModel.clearErrorMessage()
        binding.tvErrorMessage.visibility = View.GONE
        binding.btnRefresh.visibility = View.GONE
    }

    private fun observeViewModel() {
        upComingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        upComingViewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        upComingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            when {
                !errorMessage.isNullOrEmpty() -> {
                    binding.tvErrorMessage.text = errorMessage
                    binding.tvErrorMessage.visibility = View.VISIBLE
                    binding.btnRefresh.visibility = View.VISIBLE
                }
                else -> {
                    binding.tvErrorMessage.visibility = View.GONE
                    binding.btnRefresh.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
