package com.example.dicodingevent.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dicodingevent.R
import com.example.dicodingevent.adapterviewmodel.MainViewModel
import com.example.dicodingevent.adapterviewmodel.MainViewModelFactory
import com.example.dicodingevent.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(requireContext())
    }

    private var previousThemeSetting: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        val switchTheme = binding.root.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switch_theme)

        // Dapatkan pengaturan tema awal
        lifecycleScope.launch {
            val isDarkMode = settingsViewModel.getThemeSetting() // Menggunakan suspend function
            previousThemeSetting = isDarkMode
            switchTheme.isChecked = previousThemeSetting

            // Set tema sesuai pengaturan saat ini
            AppCompatDelegate.setDefaultNightMode(
                if (previousThemeSetting) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != previousThemeSetting) {
                lifecycleScope.launch {
                    settingsViewModel.saveThemeSetting(isChecked) // Simpan pengaturan tema di ViewModel
                    previousThemeSetting = isChecked
                    // Ubah tema
                    AppCompatDelegate.setDefaultNightMode(
                        if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

