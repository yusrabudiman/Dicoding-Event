package com.example.dicodingevent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.dicodingevent.R
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
        eventId.takeIf { it != -1 }?.let { detailViewModel.getEventDetail(it) }
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
            when {
                !errorMessage.isNullOrEmpty() -> {
                    binding.tvErrorMessage.text = errorMessage
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }
                else -> {
                    binding.tvErrorMessage.visibility = View.GONE
                }
            }
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.btnOpenLink.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(detail: EventDetailResponse) {
        binding.apply {
            detail.event.let { event ->
                progressBarImageLoading.visibility = View.VISIBLE
                Glide.with(this@DetailEventActivity)
                    .load(event.imageLogo)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.broken_image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBarImageLoading.visibility = View.GONE
                            imgEventLogo.setImageResource(R.drawable.broken_image)
                            imgEventLogo.setOnClickListener {
                                Glide.with(this@DetailEventActivity)
                                    .load(event.imageLogo)
                                    .placeholder(R.drawable.image_placeholder)
                                    .error(R.drawable.broken_image)
                                    .listener(this)
                                    .into(imgEventLogo)
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBarImageLoading.visibility = View.GONE
                            imgEventLogo.setOnClickListener(null)
                            return false
                        }
                    })
                    .into(imgEventLogo)
                tvEventName.text = event.name
                tvOwnerName.text = "Penyelenggara: ${event.ownerName}"
                tvBeginTime.text = "Waktu Acara: ${event.beginTime}"
                tvQuota.text = "Sisa Kuota: ${event.quota?.minus(event.registrants ?: 0)}"

                val htmlDescription = HtmlCompat.fromHtml(
                    event.description ?: "",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                tvDescription.text = htmlDescription
                tvDescription.movementMethod = LinkMovementMethod.getInstance()

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