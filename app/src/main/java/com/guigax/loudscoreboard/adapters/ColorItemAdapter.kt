package com.guigax.loudscoreboard.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ColorItemAdapter(
    context: Context,
    texts: MutableList<String>,
    private val images: MutableList<Drawable>
) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, texts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            holder = ViewHolder()
            holder.textView = view.findViewById(android.R.id.text1)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.textView.text = getItem(position)
        holder.textView.setCompoundDrawablesWithIntrinsicBounds(images[position], null, null, null)
        holder.textView.compoundDrawablePadding = 5

        return view!!
    }

    private class ViewHolder {
        lateinit var textView: TextView
    }
}