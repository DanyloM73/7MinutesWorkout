package com.danylom73.a7minutesworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.danylom73.a7minutesworkout.databinding.ItemHistoryRowBinding

class HistoryAdapter(private val items: ArrayList<String>)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemHistoryRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val date = items[position]
        holder.positionTv.text = (position + 1).toString()
        holder.itemTv.text = date

        if (position % 2 == 0) {
            holder.main.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.lightGray
                )
            )
        } else {
            holder.main.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(binding: ItemHistoryRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val positionTv = binding.positionTv
        val itemTv = binding.itemTv
        val main = binding.main
    }
}