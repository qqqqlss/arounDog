package com.example.aroundog.Model

import RecyclerViewAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.aroundog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalkRecyclerViewAdapter(private var items:ArrayList<WalkRecyclerViewItem>): RecyclerView.Adapter<WalkRecyclerViewAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view:View = v
        fun bind(item: WalkRecyclerViewItem) {
            var viewDate = view.findViewById<TextView>(R.id.walkItemDate)
            var viewSecond = view.findViewById<TextView>(R.id.walkItemSecond)
            var viewDistance = view.findViewById<TextView>(R.id.walkItemDistance)

            viewDate.text = item.date.toString()
            viewSecond.text = String.format("%.2f", item.second / 60.0) + " 분"
            viewDistance.text = item.distance.toString() + " M"

            var listener = View.OnClickListener {
                //레트로핏
                Toast.makeText(it.context, "${item.walkId}", Toast.LENGTH_SHORT).show()


            }
            view.setOnClickListener(listener)
        }
    }

    fun swap(newItems: ArrayList<WalkRecyclerViewItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.walk_item, parent, false)
        return WalkRecyclerViewAdapter.ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = items[position]

        holder.bind(item)

    }

    override fun getItemCount(): Int {
        if(!items.isEmpty())
            return items.size
        else
            return 0
    }

}