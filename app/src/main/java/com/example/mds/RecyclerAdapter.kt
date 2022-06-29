package com.example.mds

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter(private val complaintList: ArrayList<ComplaintModel>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var onItemClick : ((ComplaintModel)->Unit)? = null
    val initialComplaintList = ArrayList<ComplaintModel>().apply {
        complaintList?.let { addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return  ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return  complaintList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentComplaint = complaintList[position]

        holder.itemTitle.text = currentComplaint.title
        if(currentComplaint.status == true)
        {
            holder.itemStatus.text = "Unsolved"
        }
        else
        {
            holder.itemStatus.text = "Solved"
        }
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentComplaint)
        }


    }
    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var itemTitle: TextView
        var itemStatus: TextView
        init {
            itemTitle = itemView.findViewById(R.id.itemTitle)
            itemStatus = itemView.findViewById(R.id.itemStatus)

            itemView.setOnClickListener{
                val position: Int = adapterPosition

                Toast.makeText(itemView.context, "You clicked on ${complaintList[position].title}",Toast.LENGTH_LONG).show()

            }
        }
    }
    fun getFilter(): Filter {

        return cityFilter
    }

    private val cityFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<ComplaintModel> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialComplaintList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().toLowerCase()
                initialComplaintList.forEach {
                    if (it.title.toString().toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList

            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                complaintList.clear()
                complaintList.addAll(results.values as ArrayList<ComplaintModel>)
                notifyDataSetChanged()
            }
        }
    }

}