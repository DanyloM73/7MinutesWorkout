package com.danylom73.a7minutesworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.danylom73.a7minutesworkout.databinding.ItemExerciseStatusBinding
import androidx.core.graphics.toColorInt

class ExerciseStatusAdapter(val items: ArrayList<ExerciseModel>) :
    RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val model: ExerciseModel = items[position]
        holder.itemTv.text = model.id.toString()

        when {
            model.isSelected -> {
                holder.itemTv.background =
                    ContextCompat.getDrawable(holder.itemTv.context,
                        R.drawable.item_circular_thin_color_accent_border)
            }
            model.isCompleted -> {
                holder.itemTv.background =
                    ContextCompat.getDrawable(holder.itemTv.context,
                        R.drawable.item_circular_color_accent_background)
                holder.itemTv.setTextColor("#FFFFFF".toColorInt())
            }
            else -> {
                holder.itemTv.background =
                    ContextCompat.getDrawable(holder.itemTv.context,
                        R.drawable.item_circular_color_gray_background)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(binding: ItemExerciseStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val itemTv = binding.itemTv
    }
}