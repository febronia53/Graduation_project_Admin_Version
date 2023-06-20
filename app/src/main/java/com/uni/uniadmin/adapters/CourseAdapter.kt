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
import com.uni.uniadmin.classes.Courses
import com.uni.uniadmin.classes.MyComments
import com.uni.uniadmin.classes.Posts
import com.uni.uniadmin.classes.user.UserStudent


class CourseAdapter(
    val context: Context,
    var coursesList:MutableList<Courses>,
    val deleteCourse:(Int, Courses) ->Unit

)
    : RecyclerView.Adapter<CourseAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.course_item,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = coursesList[position]



        holder.course_assitant.text = currentItem.learningAssistant
        holder.course_name.text = currentItem.courseCode
        holder.course_code.text = currentItem.grade
        holder.course_professor.text = currentItem.professor




    }
    fun update(list: MutableList<Courses>){
        this.coursesList=list
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return coursesList.size
    }


    inner   class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val course_assitant = item.findViewById<TextView>(R.id.course_assitant)
        val course_name = item.findViewById<TextView>(R.id.course_name)
        val course_code = item.findViewById<TextView>(R.id.course_code)
        val course_professor = item.findViewById<TextView>(R.id.course_professor)

        val delete = item.findViewById<Button>(R.id.delete_bt)

        init {
            delete.setOnClickListener {
                deleteCourse.invoke(adapterPosition,coursesList[adapterPosition])
            }


        }




    }

}