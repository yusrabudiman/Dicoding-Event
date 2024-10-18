package com.example.dicodingevent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.response.EventDetailResponse
import com.example.dicodingevent.databinding.ActivityDetailEventBinding

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Detail Event"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getIntExtra("EXTRA_EVENT_ID", -1)
        if (eventId != -1) {
            detailViewModel.getEventDetail(eventId)
        }
        observeViewModel()
    }
    private fun observeViewModel() {
        detailViewModel.eventDetail.observe(this) { eventDetail ->
            binding.progressBar.visibility = View.GONE

            eventDetail?.let {
                setupUI(it)
                binding.btnOpenLink.visibility = View.VISIBLE
            }
        }
        detailViewModel.errorMessage.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE

            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                binding.tvErrorMessage.text = errorMessage
                binding.tvErrorMessage.visibility = View.VISIBLE
            } else {
                binding.tvErrorMessage.visibility = View.GONE
            }
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnOpenLink.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(detail: EventDetailResponse) {
        binding.apply {
            detail.event.let { event ->
                Glide.with(this@DetailEventActivity).load(event.imageLogo).into(imgEventLogo)
                tvEventName.text = event.name
                tvOwnerName.text = "By: ${event.ownerName}"
                tvBeginTime.text = "Time: ${event.beginTime}"
                tvQuota.text = "Quota Left: ${event.quota?.minus(event.registrants ?: 0)}"
                tvDescription.text = HtmlCompat.fromHtml(
                    event.description ?: "",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                btnOpenLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
