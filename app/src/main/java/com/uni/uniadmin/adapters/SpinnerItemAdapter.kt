package com.uni.uniadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.PermissionItem
import com.uni.uniadmin.classes.SpinnerItem

class SpinnerItemAdapter(
    val context: Context,
    var itemList:MutableList<SpinnerItem>
    )   : BaseAdapter() {


    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: myViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_item_custom, parent, false)
            vh = myViewHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as myViewHolder
        }
        val currentItem = itemList[position]
        vh.upper.text = currentItem.textUpperLeft
        vh.down.text = currentItem.textDownLeft
        vh.center.text = currentItem.rightCenter




        return view

    }

    fun update(list: MutableList<SpinnerItem>){
        this.itemList=list
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return itemList.size
    }



    inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val upper = item.findViewById<TextView>(R.id.upper_left)
        val down = item.findViewById<TextView>(R.id.down_left)
        val center = item.findViewById<TextView>(R.id.center_right)

        }



    override fun getItem(p0: Int): Any {
        return itemList[p0];
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong();
    }



}





