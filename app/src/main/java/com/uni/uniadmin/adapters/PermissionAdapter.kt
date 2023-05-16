package com.uni.uniadmin.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.PermissionItem


class PermissionAdapter(
    val context: Context,
    var permissionList:MutableList<PermissionItem>,
    val delete:(Int, PermissionItem) ->Unit

)
    : RecyclerView.Adapter<PermissionAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.permission_item,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = permissionList[position]



        holder.name.text = currentItem.studentName
        holder.section.text = currentItem.studentSection
        holder.departemt.text = currentItem.studentDep
        holder.grade.text = currentItem.studentDep
        holder.permission.text = currentItem.permissionMessage





    }
    fun update(list: MutableList<PermissionItem>){
        this.permissionList=list
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return permissionList.size
    }


    inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val name = item.findViewById<TextView>(R.id.student_name_perm)
        val grade = item.findViewById<TextView>(R.id.student_grade_perm)
        val departemt = item.findViewById<TextView>(R.id.student_department_perm)
        val section = item.findViewById<TextView>(R.id.student_section_perm)
        val permission = item.findViewById<TextView>(R.id.permission_message_text)


        val delete_bt = item.findViewById<ConstraintLayout>(R.id.delete_perm)

        init {
            delete_bt.setOnClickListener {
                delete.invoke(adapterPosition,permissionList[adapterPosition])
            }


        }




    }

}