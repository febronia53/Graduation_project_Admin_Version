package com.uni.uniadmin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.Comment
import com.uni.uniadmin.classes.MyComments
import com.uni.uniadmin.data.Resource
import com.uni.uniadmin.data.di.PostType
import com.uni.uniadmin.databinding.FragmentCommentBinding
import com.uni.uniadmin.ui.HomeScreen
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
    private lateinit var binding: FragmentCommentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(layoutInflater)
        //++++++++++++++++++++++++++//
        val bottomNavigationView =  (activity as HomeScreen).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE
        binding.backFragmentBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
            bottomNavigationView.visibility = View.VISIBLE
        }
        //+++++++++++++++++++++++++//

        // update user data --------------------------------------------------------------------------------
        authViewModel.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
            } else {
                Toast.makeText(
                    context,
                    "error on loading user data please refresh the current screen ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // update user data --------------------------------------------------------------------------------



        val recyclerView = binding.commentRecycler
        progress = binding.progressBarComment
        commentList = arrayListOf()

        adapter = CommentAdapter(requireContext(), commentList,

            onUpdate = { pos,comment->
                val com = Comment(comment.commentID,currentUser.code,comment.description,currentUser.name,comment.time)

                when (aud) {
                    PostType.course -> {
                        viewModel.deleteCommentsCourse(com,postID ,courseID)
                    }
                    PostType.personal_posts -> {
                        viewModel.deleteCommentsPersonal(com,postID, currentUser.code)
                    }

                    PostType.section_posts -> {
                        viewModel.deleteCommentsSection(com,postID, sectionH, departmentH)
                    }

                    PostType.general -> {
                        viewModel.deleteCommentsGeneral(com,postID)
                    }

                }
                commentText.setText(comment.description)
            }
            , onDelete = { pos,comment->

                val com = Comment(comment.commentID,currentUser.code,comment.description,currentUser.name,comment.time)
                when (aud) {
                    PostType.course -> {
                        viewModel.deleteCommentsCourse(com,postID ,courseID)
                    }
                    PostType.personal_posts -> {
                        viewModel.deleteCommentsPersonal(com,postID, currentUser.code)
                    }

                    PostType.section_posts -> {
                        viewModel.deleteCommentsSection(com,postID, sectionH, departmentH)
                    }

                    PostType.general -> {
                        viewModel.deleteCommentsGeneral(com,postID)
                    }
                }
                observeDeletedComment()
            })

        val args = this.arguments
        if (args != null) {

            postID = args.getString("postId", "")
            aud = args.getString("aud", "")
            courseID = args.getString("course", "")
        }

        when (aud) {
            PostType.course -> {
                viewModel.getCommentsCourse(postID, courseID)
            }

            PostType.personal_posts -> {
                viewModel.getCommentsPersonal(postID, currentUser.code)
            }

            PostType.section_posts -> {
                viewModel.getCommentsSection(postID, sectionH, departmentH)
            }

            PostType.general -> {
                viewModel.getCommentsGeneral(postID)

            }

        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        if (commentList.size == 0) {
            binding.NoCommentsYet.visibility = View.VISIBLE
        } else {
            binding.NoCommentsYet.visibility = View.GONE
        }
        observeCommentGeneral()

        return binding.root
    }

    private fun observeDeletedComment() {
        lifecycleScope.launchWhenCreated {
            viewModel.addCommentGeneral.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.result, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val send = view.findViewById<ImageButton>(R.id.send_comment_bt)
        commentText = view.findViewById(R.id.comment_ed_text)
        var comment = ""
        send.setOnClickListener {
            comment = commentText.text.toString()
            commentText.text.clear()
            when (aud) {
                PostType.course -> {
                    viewModel.addCommentsCourse(
                        Comment("",currentUser.code, comment, currentUser.name, Date()),
                        postID,
                        courseID
                    )

                }

                PostType.personal_posts -> {
                    viewModel.addCommentsPersonal(
                        Comment("",currentUser.code, comment, currentUser.name, Date()),
                        postID,
                        currentUser.userId
                    )

                }

                PostType.section_posts -> {
                    viewModel.addCommentsSection(
                        Comment("",currentUser.code, comment, currentUser.name, Date()),
                        postID,
                     sectionH,
                        departmentH
                    )

                }

                PostType.general -> {
                    viewModel.addCommentsGeneral(
                        Comment("",currentUser.code, comment, currentUser.name, Date()),
                        postID
                    )
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
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.result, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Failure -> {
                        progress.visibility = View.GONE
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
            viewModel.getCommentGeneral.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        progress.visibility = View.GONE
                        commentList.clear()
                        state.result.forEach {
                            val comment=MyComments(it.commentID,it.description,it.authorName,it.userID,false,it.time)
                            if (it.userID == currentUser.userId){
                                comment.myComment=true
                            }
                            binding.NoCommentsYet.visibility = View.GONE
                            commentList.add(comment)
                        }
                        adapter.update(commentList)
                    }
                    is Resource.Failure -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }

    }

}
