package com.example.architectureproject.ui.detailworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.R
import com.example.architectureproject.databinding.ExerciseItemBinding
import com.example.archtectureproject.data.model.Exercise

val white_a = Color.parseColor("#90FFFFFF")

class SelectedWorkoutAdapter(val items:MutableList<Exercise>, private val callback: ItemListener) : RecyclerView.Adapter<SelectedWorkoutAdapter.ItemViewHolder>() {

    private val selectedExercises = mutableListOf<Exercise>()
    interface ItemListener {
        fun onItemClicked(index:Int)
    }

    inner class ItemViewHolder(private val binding: ExerciseItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

        }

        fun bind(exercise: Exercise) {
            binding.tvExerciseName.text = exercise.name
            binding.weightNum.text = exercise.weight.toString()
            binding.tvExerciseDescription.text = "#${exercise.muscleGroup}"
            binding.cardView.setBackgroundResource(R.drawable.card_white)
            binding.checkBox.visibility = View.VISIBLE
            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    binding.cardView.alpha = 0.5f
                } else {
                    binding.cardView.alpha = 1.0f
                }
            }
            Glide.with(binding.root).load(exercise.iconUri).into(binding.exerciseIcon)
        }

    }


    fun itemAt(position: Int) = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(ExerciseItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}