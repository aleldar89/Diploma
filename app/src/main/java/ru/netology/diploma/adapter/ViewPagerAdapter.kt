package ru.netology.diploma.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.diploma.ui.event_fragments.EventsFeedFragment
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostsFeedFragment()
            else -> EventsFeedFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}