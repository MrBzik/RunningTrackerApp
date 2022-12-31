package com.example.runningtrackerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.ItemRunBinding
import com.example.runningtrackerapp.db.entities.Run
import com.example.runningtrackerapp.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunHolder>()  {

    class RunHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        val bind : ItemRunBinding

        init {

            bind = ItemRunBinding.bind(itemView)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_run, parent, false)

        return RunHolder(view)

    }

    override fun onBindViewHolder(holder: RunHolder, position: Int) {

        val currentRun = differ.currentList[position]

        holder.bind.apply {


            val avgSpeed = "${currentRun.avgSpeedInKm}km/h"

            tvAvgSpeed.text = avgSpeed

            val distance = "${ currentRun.distanceInMeters / 1000f}km"

            tvDistance.text = distance

            tvTime.text = Utils.timerFormat(currentRun.runningTimeInMillis, false)

            tvCalories.text = "${ currentRun.calBurned }Kcal"


            val date = Calendar.getInstance().apply {
                timeInMillis = currentRun.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

            tvDate.text = dateFormat.format(date.time)

        }
        Glide.with(holder.itemView).load(currentRun.img).into(holder.bind.ivRunImage)

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {

           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

        var differ = AsyncListDiffer(this, diffCallback)


        fun submitList(list: List<Run>) = differ.submitList(list)
}