package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Comment
import com.uni.uniadmin.classes.MyComments
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.PostType
import com.uni.uniadmin.viewModel.AuthViewModel
import com.uni.uniadmin.viewModel.FirebaseViewModel
import com.uni.uniteaching.adapters.CommentAdapter
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class CommentFragment : Fragment() {

    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var progress: ProgressBar
    lateinit var currentUser: UserAdmin

    lateinit var postID:String
    lateinit var courseID:String
    lateinit var studentID:String
    lateinit var sectionH:String
    lateinit var departmentH:String

    lateinit var aud :String
    lateinit var commentText :EditText

    lateinit var  adapter : CommentAdapter
    lateinit var commentList:MutableList<MyComments>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // update user data --------------------------------------------------------------------------------
        authViewModel.getSessionStudent {user->
            if (user !=null){
                currentUser=user
                Toast.makeText(context,currentUser.name, Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context,"error on loading user data please refresh the current screen ", Toast.LENGTH_LONG).show()
            }
        }
        // update user data --------------------------------------------------------------------------------
        val view= inflater.inflate(R.layout.fragment_comment, container, false)


        val recyclerView = view.findViewById<RecyclerView>(R.id.comment_recycler)
        progress = view.findViewById<ProgressBar>(R.id.progress_par_comment)


        commentList = arrayListOf()


        adapter= CommentAdapter(requireContext(),commentList,
            onUpdate = { pos,item->
                var comment = ""
                if (item.myComment){
                    comment=commentText.text.toString()
                    when(aud){
                        PostType.course->{
                            viewModel.updateCommentsCourse(Comment(item.commentID,currentUser.userId,comment,currentUser.name, Date()), postID,courseID)
                        }
                        PostType.personal_posts->{
                            viewModel.updateCommentsPersonal(Comment(item.commentID,currentUser.userId,comment,currentUser.name, Date()),postID,studentID)

                        }
                        PostType.section_posts->{
                            viewModel.updateCommentsSection(Comment(item.commentID,currentUser.userId,comment,currentUser.name, Date()),postID,sectionH,departmentH)

                        }
                        PostType.general->{
                            viewModel.updateCommentsGeneral(Comment(item.commentID,currentUser.userId,comment,currentUser.name, Date()),postID)
                        }

                    }
                }
            },
            onDelete = {pos,item->
                if (item.myComment){
                    when(aud){
                        PostType.course->{
                            viewModel.deleteCommentsCourse(Comment(item.commentID,currentUser.userId,"",currentUser.name, Date()), postID,courseID)
                        }
                        PostType.personal_posts->{
                            viewModel.deleteCommentsPersonal(Comment(item.commentID,currentUser.userId,"",currentUser.name, Date()),postID,studentID)

                        }
                        PostType.section_posts->{
                            viewModel.deleteCommentsSection(Comment(item.commentID,currentUser.userId,"",currentUser.name, Date()),postID,sectionH,departmentH)

                        }
                        PostType.general->{
                            viewModel.deleteCommentsGeneral(Comment(item.commentID,currentUser.userId,"",currentUser.name, Date()),postID)
                        }

                    }
                }
            }
        )

        val args= this.arguments
        if (args != null) {

            postID = args.getString("postId","")
            aud = args.getString("aud","")
            courseID = args.getString("course","")
            studentID = args.getString("studentID","")
            sectionH = args.getString("section","")
            departmentH = args.getString("department","")


        }

        when(aud){
            PostType.course->{
                viewModel.getCommentsCourse(postID,courseID)
                //observeCommentCourse()
            }
            PostType.personal_posts->{
                viewModel.getCommentsPersonal(postID,studentID)
                //  observeCommentPersonal()
            }
            PostType.section_posts->{
                viewModel.getCommentsSection(postID,sectionH,departmentH)
                //   observeCommentSection()
            }
            PostType.general->{
                viewModel.getCommentsGeneral(postID)
                //   observeCommentGeneral()
            }

        }
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=adapter


        observeCommentGeneral()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val send=view.findViewById<Button>(R.id.send_comment_bt)
        commentText=view.findViewById<EditText>(R.id.comment_ed_text)
        var comment = ""
        send.setOnClickListener {
            comment=commentText.text.toString()
            when(aud){
                PostType.course->{
                    viewModel.addCommentsCourse(Comment("",currentUser.userId,comment,currentUser.name, Date()), postID,courseID)

                }
                PostType.personal_posts->{
                    viewModel.addCommentsPersonal(Comment("",currentUser.userId,comment,currentUser.name, Date()),postID,studentID)

                }
                PostType.section_posts->{
                    viewModel.addCommentsSection(Comment("",currentUser.userId,comment,currentUser.name, Date()),postID,sectionH,departmentH)

                }
                PostType.general->{
                    viewModel.addCommentsGeneral(Comment("",currentUser.userId,comment,currentUser.name, Date()),postID)
                }

            }
            observeAddingComment()
        }

    }
    private fun observeAddingComment() {
        lifecycleScope.launchWhenCreated {
            viewModel.addCommentGeneral.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }
                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.result, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }

        }
    }
    private fun observeCommentGeneral() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCommentGeneral.collectLatest {state->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility=View.VISIBLE

                    }
                    is Resource.Success -> {
                        progress.visibility=View.INVISIBLE
                        commentList.clear()
                        state.result.forEach {
                            val comment=MyComments(it.commentID,it.description,it.authorName,false,it.time)
                            if (it.userID==currentUser.userId){
                                comment.myComment=true
                            }
                            commentList.add(comment)
                        }
                        adapter.update(commentList)

                    }
                    is Resource.Failure -> {
                        progress.visibility=View.INVISIBLE
                        Toast.makeText(context,state.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }

    }
}
