package com.example.architectureproject.ui.allExercises

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.databinding.MuscleGroupItemBinding
import com.example.architectureproject.ui.viewModels.MuscleGroupItemModel

class MuscleGroupAdapter(private val muscleItems: List<MuscleGroupItemModel>, private val callback: MuscleGroupListener) :
    RecyclerView.Adapter<MuscleGroupAdapter.MuscleGroupViewHolder>() {

    interface MuscleGroupListener {
        fun onItemClicked(index:Int)
        fun onItemLongClick(index:Int)
    }

    inner class MuscleGroupViewHolder(private val binding: MuscleGroupItemBinding) :
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

        fun bind(muscleGroupItemModel: MuscleGroupItemModel) {
            binding.muscleGroup.text = muscleGroupItemModel.muscleType
            Glide.with(binding.root).load(muscleGroupItemModel.imageSrc).into(binding.muscleIcon)
        }
    }


    fun itemAt(position: Int) = muscleItems[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MuscleGroupViewHolder(MuscleGroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: MuscleGroupViewHolder, position: Int) = holder.bind(muscleItems[position])

    override fun getItemCount() = muscleItems.size
}