package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.adapter.ViewPagerAdapter
import ru.netology.diploma.databinding.FragmentViewpagerBinding

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewpagerBinding.inflate(inflater, container, false)
        val adapter = ViewPagerAdapter(requireActivity())

        binding.viewPager.adapter = adapter
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf("Posts", "Events")
            tab.text = tabNames[position]
        }.attach()

        return binding.root
    }
}