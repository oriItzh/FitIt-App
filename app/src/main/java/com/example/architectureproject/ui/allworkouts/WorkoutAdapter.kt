package com.example.architectureproject.ui.allworkouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.databinding.WorkoutLayoutBinding
import com.example.archtectureproject.data.model.Workout

class WorkoutAdapter(val items:List<Workout>, private val callback: ItemListener) : RecyclerView.Adapter<WorkoutAdapter.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index:Int)
        fun onItemLongClick(index:Int)
    }

    inner class ItemViewHolder(private val binding: WorkoutLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

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

        fun bind(workout: Workout) {
            binding.workoutTitle.text = workout.title
            val times: List<Int> = workout.totalEstimatedTime()
            binding.workoutHours.text = times[0].toString()
            binding.workoutMinutes.text = times[1].toString()
        }
    }


    fun itemAt(position: Int) = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(WorkoutLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}