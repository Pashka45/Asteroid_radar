package com.udacity.asteroidradar.main

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapters.AsteroidsAdapter
import com.udacity.asteroidradar.adapters.AsteroidsListener
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.enums.AsteroidFilter


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    private lateinit var adapter: AsteroidsAdapter

    private val observerCallBack = Observer<List<Asteroid>> {
        it?.let {
            adapter.addList(it)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        adapter = AsteroidsAdapter(AsteroidsListener {
            viewModel.goToDetailFragment(it)
        })

        viewModel.navigationToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.goneToDetailFragment()
            }
        })

        binding.asteroidRecycler.adapter = adapter

        viewModel.imageOfTheDay.observe(viewLifecycleOwner, Observer {
            it?.let {
                Picasso
                    .with(context)
                    .load(it.url)
                    .error(R.drawable.placeholder_picture_of_day)
                    .into(binding.activityMainImageOfTheDay)
            }
        })

        viewModel.getAsteroidsByFilter(AsteroidFilter.ALL).observe(viewLifecycleOwner, observerCallBack)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.getAsteroidsByFilter(
            when (item.itemId) {
                R.id.show_today_menu -> AsteroidFilter.TODAY
                R.id.show_week_menu -> AsteroidFilter.WEEK
                else -> AsteroidFilter.ALL
            }
        ).observe(viewLifecycleOwner, observerCallBack)
        return true
    }
}