package com.uni.uniadmin.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.MyComments
import com.uni.uniadmin.classes.Posts
import com.uni.uniadmin.classes.user.UserStudent


class StudentAdapter(
    val context: Context,
    var studentList:MutableList<UserStudent>,
    val removePerm:(Int, UserStudent) ->Unit,
    val itemClick:(Int, UserStudent) ->Unit,
    val addPerm:(Int, UserStudent) ->Unit

)
    : RecyclerView.Adapter<StudentAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.student_item,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = studentList[position]



        holder.name.text = currentItem.name
        holder.section.text = currentItem.section
        holder.departemt.text = currentItem.department
        holder.grade.text = currentItem.grade




    }
    fun update(list: MutableList<UserStudent>){
        this.studentList=list
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return studentList.size
    }


    inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val name = item.findViewById<TextView>(R.id.student_name)
        val grade = item.findViewById<TextView>(R.id.student_grade)
        val departemt = item.findViewById<TextView>(R.id.student_department)
        val section = item.findViewById<TextView>(R.id.student_section)

        val add = item.findViewById<Button>(R.id.add_perm)
        val remove = item.findViewById<Button>(R.id.remove_permission_all)
        val item = item.findViewById<ConstraintLayout>(R.id.student_item)
        init {
            remove.setOnClickListener {
                removePerm.invoke(adapterPosition,studentList[adapterPosition])
            }
            add.setOnClickListener {
                addPerm.invoke(adapterPosition,studentList[adapterPosition])
            }

            item.setOnClickListener {
                itemClick.invoke(adapterPosition,studentList[adapterPosition])
            }
        }




    }

}