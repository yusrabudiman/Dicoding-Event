package com.example.dicodingevent.adapterviewmodel


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.ListEventHorizontalBinding

class ReviewHorizontalAdapter(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<ListEventsItem, ReviewHorizontalAdapter.HorizontalAdapterViewHolder>(ListEventsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalAdapterViewHolder {
        val binding =
            ListEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalAdapterViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HorizontalAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HorizontalAdapterViewHolder(
        private val binding: ListEventHorizontalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.titleEvent.text = event.name
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
        }
    }
}
