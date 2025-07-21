package com.example.architectureproject.ui.createworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.R
import com.example.architectureproject.databinding.ExerciseItemBinding
import com.example.archtectureproject.data.model.Exercise

val darkBlueAlpha = Color.parseColor("#CC69A2F8")

class ExercisesListAdapter(var items:List<Exercise>, private val callback: ItemListener) : RecyclerView.Adapter<ExercisesListAdapter.ItemViewHolder>() {

    // Selected Workouts
    private val selectedExercises = mutableListOf<Exercise>()

    interface ItemListener {
        fun onItemClicked(index:Int)
    }

    inner class ItemViewHolder(private val binding: ExerciseItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            callback.onItemClicked(adapterPosition)

            if (selectedExercises.contains(items[adapterPosition])) {
                selectedExercises.remove(items[adapterPosition])
            } else {
                selectedExercises.add(items[adapterPosition])
            }
            notifyItemChanged(adapterPosition)
        }


        fun bind(exercise: Exercise) {
            binding.tvExerciseName.text = exercise.name
            binding.weightNum.text = exercise.weight.toString()
            binding.tvExerciseDescription.text = "#${exercise.muscleGroup}"
            Glide.with(binding.root).load(exercise.iconUri).into(binding.exerciseIcon)
            // Set the background color based on selected state
            if (selectedExercises.contains(items[adapterPosition])) {
                binding.cardView.setBackgroundResource(R.drawable.stroke_card)
            } else {
                binding.cardView.setBackgroundResource(R.drawable.card)
            }
        }
    }


    fun itemAt(position: Int) = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(ExerciseItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun getChosenItems() = selectedExercises.toList()

    fun updateItems(newItems: List<Exercise>) {
        items = newItems
        notifyDataSetChanged()
    }

}