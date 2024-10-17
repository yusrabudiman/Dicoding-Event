package com.example.dicodingevent.adapterviewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.ListEventVerticalBinding

class ReviewVerticalAdapter(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<ListEventsItem, ReviewVerticalAdapter.VerticalAdapterViewHolder>(ListEventsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalAdapterViewHolder {
        val binding =
            ListEventVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalAdapterViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VerticalAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VerticalAdapterViewHolder(
        private val binding: ListEventVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.titleEvent.text = event.name
            binding.descriptionEvent.text = event.summary
            Glide.with(binding.imageEvent.context)
                .load(event.imageLogo)
                .into(binding.imageEvent)

            binding.root.setOnClickListener {
                onItemClick?.invoke(event.id)
            }
        }
    }

    class ListEventsDiffUtil : DiffUtil.ItemCallback<ListEventsItem>() {
        override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
        }
    }
}