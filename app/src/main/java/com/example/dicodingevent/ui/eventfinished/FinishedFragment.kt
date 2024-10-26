package com.example.dicodingevent.ui.eventfinished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.adapterviewmodel.MainViewModel
import com.example.dicodingevent.adapterviewmodel.MainViewModelFactory
import com.example.dicodingevent.adapterviewmodel.ReviewVerticalAdapter
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.google.android.material.search.SearchView

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private val finishedViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(requireContext())
    }
    private lateinit var finishedAdapter: ReviewVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the finished adapter
        finishedAdapter = ReviewVerticalAdapter { eventId ->
            val intent = Intent(context, DetailEventActivity::class.java)
            intent.putExtra("EXTRA_EVENT_ID", eventId)
            context?.startActivity(intent)
        }

        // Setup RecyclerView for finished events
        binding.rvFinishedEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvent.adapter = finishedAdapter

        // Set up SearchBar and SearchView
        setupSearchBar()

        // LiveData observation
        observeViewModel()

        // Fetch data from ViewModel
        finishedViewModel.getFinishedEvents()

        // Handle Try Again button click
        binding.btnTryAgain.setOnClickListener {
            resetErrorMessage()
            finishedViewModel.getFinishedEvents()
        }
    }

    private fun resetErrorMessage() {
        finishedViewModel.clearErrorMessage()
        binding.tvErrorMessage.visibility = View.GONE
    }

    private fun setupSearchBar() {
        val searchBar = binding.searchBar
        val searchView = binding.searchView

        searchBar.setOnClickListener {
            searchView.visibility = View.VISIBLE
            searchView.show()
        }

        // Handle SearchView query submission
        searchView.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchView.text.toString().trim() // Trim to avoid accidental spaces
                performSearch(query)
                searchView.hide()
                if (query.isEmpty()) {
                    searchBar.hint = getString(R.string.devcoach)
                } else {
                    searchBar.hint = query
                }
                true
            } else {
                false
            }
        }

        // Hide SearchView when dismissed
        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                searchView.visibility = View.GONE
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { finishedEvents ->
                finishedAdapter.submitList(finishedEvents)
                binding.rvFinishedEvent.scrollToPosition(0) // Reset scroll to top
                binding.tvNoResults.visibility = View.GONE
                binding.btnTryAgain.visibility = View.GONE
            }
        } else {
            finishedViewModel.searchFinishedEvents(query).observe(viewLifecycleOwner) { searchResults ->
                if (searchResults.isNullOrEmpty()) {
                    binding.tvErrorMessage.visibility = View.GONE
                    binding.tvNoResults.visibility = View.VISIBLE
                    binding.btnTryAgain.visibility = View.GONE
                } else {
                    binding.tvNoResults.visibility = View.GONE
                    binding.tvErrorMessage.visibility = View.GONE
                    binding.btnTryAgain.visibility = View.GONE
                    finishedAdapter.submitList(searchResults)
                }
                binding.rvFinishedEvent.scrollToPosition(0) // Reset scroll to top
            }
        }
    }

    private fun observeViewModel() {
        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { finishedEvents ->
            finishedEvents?.let {
                finishedAdapter.submitList(it)
                binding.rvFinishedEvent.scrollToPosition(0)
            }
        }

        finishedViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        finishedViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                binding.tvErrorMessage.text = errorMessage
                binding.tvErrorMessage.visibility = View.VISIBLE
                binding.btnTryAgain.visibility = View.VISIBLE
            } else {
                binding.tvErrorMessage.visibility = View.GONE
                binding.btnTryAgain.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
