package com.udacity.asteroidradar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidListItemDescriptions
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding
import com.udacity.asteroidradar.databinding.AsteroidListItemBindingImpl
import kotlinx.android.synthetic.main.asteroid_list_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsteroidsAdapter(private val listener: AsteroidsListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(AsteroidsDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addList(list: List<Asteroid>) {
        adapterScope.launch {
            val items = list.map {
                    DataItem.AsteroidItem(it)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) as DataItem.AsteroidItem

        if (holder is ViewHolder) {
            holder.bind(item.asteroid, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(private val binding: AsteroidListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Asteroid, listener: AsteroidsListener) {
            binding.asteroid = item
            binding.listener = listener

            val context = binding.listItem.context
            val asteroidDesc = AsteroidListItemDescriptions(
                String.format(context.getString(R.string.codename_description), item.codename),
                String.format(context.getString(R.string.close_approach_data_description), item.closeApproachDate),
                String.format(context.getString(if (item.isPotentiallyHazardous) R.string.potentially_hazardous_asteroid_image
                else R.string.not_hazardous_asteroid_image))
            )

            binding.asteroidDesc = asteroidDesc
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidListItemBindingImpl.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}