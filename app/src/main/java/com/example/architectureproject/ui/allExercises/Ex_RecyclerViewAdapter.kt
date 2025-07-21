package com.example.architectureproject.ui.allExercises
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.databinding.ExerciseItemBinding
import com.example.architectureproject.ui.viewModels.ExerciseViewModel

import com.example.archtectureproject.data.model.Exercise

class Ex_RecyclerViewAdapter(val exercises:List<Exercise>, private val viewModel: ExerciseViewModel, private val callback: ExerciseListener) :
    RecyclerView.Adapter<Ex_RecyclerViewAdapter.ExerciseViewHolder>() {

    interface ExerciseListener {
        fun onItemClicked(index:Int)
        fun onItemLongClick(index:Int)
    }

    inner class ExerciseViewHolder(private val binding: ExerciseItemBinding) :
        RecyclerView.ViewHolder(binding.root),View.OnClickListener,View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            callback.onItemClicked(adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            callback.onItemLongClick(adapterPosition)
            return true
        }

        fun bind(exercise: Exercise) {
            // Get the current drawable from the ImageView
            val currentDrawable = binding.exerciseIcon.drawable

            // Get the drawable from the resource ID
            val requiredDrawable =
                ContextCompat.getDrawable(binding.exerciseIcon.context, exercise.iconUri)

            // Compare the drawables using constantState
            val isIconChanged = currentDrawable != requiredDrawable

            // Determine if there is any change
            viewModel.isChanged = binding.tvExerciseName.text.toString() != exercise.name ||
                    binding.weightNum.text != exercise.weight.toString() ||
                    isIconChanged

            binding.tvExerciseName.setText(exercise.name)
            binding.weightNum.text = exercise.weight.toString()
            binding.tvExerciseDescription.text = "#${exercise.muscleGroup}"
            Glide.with(binding.exerciseIcon.context).load(exercise.iconUri).circleCrop()
                .into(binding.exerciseIcon)
        }
    }

    fun itemAt(position: Int) = exercises[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExerciseViewHolder(ExerciseItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) = holder.bind(exercises[position])

    override fun getItemCount() = exercises.size
}