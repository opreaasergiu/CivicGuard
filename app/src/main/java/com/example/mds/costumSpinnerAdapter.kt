package com.example.mds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_complaint.view.*
import kotlinx.android.synthetic.main.custom_spinner_item.view.*


class customSpinnerAdapter(ctx: Context, countries: ArrayList<String>) : ArrayAdapter<String>(ctx, 0, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getItem(position: Int): String? {
        return super.getItem(position)
    }

    fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
        val country = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
           // android.R.layout.simple_spinner_item,
            R.layout.custom_spinner_item,
            parent,
            false
        )

        country?.let {
            view.customTextView.text = country.toString()
        }
        return view
    }
}