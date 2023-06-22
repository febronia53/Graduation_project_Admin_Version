package com.uni.uniadmin.data


import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.uni.uniadmin.classes.*
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.classes.Professor
import com.uni.uniadmin.data.di.FireStoreTable
import com.uni.uniadmin.data.di.PermissionsRequired
import com.uni.uniadmin.data.di.PostType
import com.uni.uniadmin.data.di.UserTypes

import javax.inject.Inject

class FirebaseRepoImp @Inject constructor(
    private val database: FirebaseFirestore

) : FirebaseRepo {


    // ------------------------------------------------ search for student -------------------------------------------------------//
    override suspend fun searchStudentByID(
        grade: String,
        code: String,
        result: (Resource<UserStudent>) -> Unit
    ) {
        val docRef = database.collection(grade)
            .whereEqualTo("code", code)
        docRef.get()
            .addOnSuccessListener {
                lateinit var student: UserStudent
                for (rec in it) {
                    student = rec.toObject(UserStudent::class.java)
                }
                result.invoke(
                    Resource.Success(student)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun searchStudentBySection(
        grade: String,
        section: String,
        department: String,
        result: (Resource<List<UserStudent>>) -> Unit
    ) {
        val docRef = database.collection(grade)
            .whereEqualTo("section", section)
            .whereEqualTo("department", department)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<UserStudent>()
                for (rec in it) {
                    val student = rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun searchStudentByDepartment(
        grade: String,
        department: String,
        result: (Resource<List<UserStudent>>) -> Unit
    ) {
        val docRef = database.collection(grade)
            .whereEqualTo("department", department)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<UserStudent>()
                for (rec in it) {
                    val student = rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun searchStudentAll(
        grade: String,
        result: (Resource<List<UserStudent>>) -> Unit
    ) {
        val docRef = database.collection(grade)

        docRef.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<UserStudent>()
                for (rec in it) {
                    val student = rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
// ----------------------------------------------- search for student --------------------------------------------------------//


    // ------------------------------------------------------- permission --------------------------------------------------------//
    override suspend fun addPermission(
        grade: String,
        permission: PermissionItem,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PermissionsRequired.sing_in_permission).document()
        permission.permissionId = document.id
        document.set(permission)
            .addOnSuccessListener {
                val state = updateUserPermission(permission.userId, permission.studentGrade, false)
                if (state == 1) {
                    result.invoke(
                        Resource.Success("asking for permission")
                    )
                } else {
                    result.invoke(
                        Resource.Success("added but user data did not updated")
                    )
                }

            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    private fun updateUserPermission(userId: String, grade: String, value: Boolean): Int {
        var state = 0
        val docRef = database.collection(grade).document(userId)
        docRef.update("hasPermission", value).addOnSuccessListener {
            state = 1
            Log.e("update", "I am here ")
        }
            .addOnFailureListener {
                state = -1
            }
        return state
    }

    override suspend fun deletePermission(
        permission: PermissionItem,
        result: (Resource<String>) -> Unit
    ) {


        val docRef = database.collection(permission.studentGrade).document(permission.userId)
        val documentDelete = database.collection(PermissionsRequired.sing_in_permission)
            .document(permission.permissionId)
        val documentCount = database.collection(PermissionsRequired.sing_in_permission)
            .whereEqualTo("userId", permission.userId).count()

        documentCount.get(AggregateSource.SERVER)
            .addOnSuccessListener {
                val count = it.count
                Log.e("count", count.toString())
                if (count < 2) {
                    documentDelete.delete()
                        .addOnSuccessListener {
                            docRef.update("hasPermission", true)
                                .addOnSuccessListener {
                                    Log.e("delete updat", count.toString())
                                    result.invoke(
                                        Resource.Success("all done object deleted")
                                    )
                                }
                                .addOnFailureListener {
                                    result.invoke(
                                        Resource.Success("there is a problim with updating")
                                    )
                                }
                        }
                        .addOnFailureListener {
                            result.invoke(
                                Resource.Success(it.localizedMessage)
                            )
                        }
                } else {
                    documentDelete.delete()
                        .addOnSuccessListener {
                            Log.e("delete nooooo update", count.toString())
                            result.invoke(
                                Resource.Success("deleted successfully")
                            )
                        }
                        .addOnFailureListener {
                            result.invoke(
                                Resource.Success(it.localizedMessage)
                            )
                        }

                }

            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Success(it.localizedMessage)
                )
            }

        /* val count=getPermissionCount(permission.userId)
        val document=database.collection(PermissionsRequired.sing_in_permission).document(permission.permissionId)
        document.delete()
            .addOnSuccessListener {
               if(count<2){
                   Log.e("count",count.toString())
                   val state =updateUserPermission(permission.userId,permission.studentGrade,true)
                   Log.e("repo","I am here ")
                   Log.e("state",state.toString())
                   if (state ==1){
                       result.invoke(
                           Resource.Success("done")
                       )
                   }else{
                       result.invoke(
                           Resource.Success("deleted but user data did not updated")
                       )
                   }
               }else{
                   result.invoke(
                       Resource.Success("done")

                   )
               }

            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }*/

    }

    override suspend fun getPermission(
        grade: String,
        result: (Resource<List<PermissionItem>>) -> Unit
    ) {
        val document = database.collection(PermissionsRequired.sing_in_permission)
            .whereEqualTo("studentGrade", grade)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val permissions = arrayListOf<PermissionItem>()
            for (rec in snapshot!!) {
                val post = rec.toObject(PermissionItem::class.java)
                permissions.add(post)
            }
            result.invoke(Resource.Success(permissions))
        }
    }

    override suspend fun getPermissionByUserId(
        userId: String,
        result: (Resource<List<PermissionItem>>) -> Unit
    ) {
        val document = database.collection(PermissionsRequired.sing_in_permission)
            .whereEqualTo("userId", userId)

        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val permissions = arrayListOf<PermissionItem>()
            for (rec in snapshot!!) {
                val post = rec.toObject(PermissionItem::class.java)
                permissions.add(post)
            }
            result.invoke(Resource.Success(permissions))
        }

    }
    /* fun getPermissionCount(userId:String):Long {
         var count:Long=-1
         val document=database.collection(PermissionsRequired.sing_in_permission)
             .whereEqualTo("userId", userId).count()
         document.get(AggregateSource.SERVER)
             .addOnSuccessListener {
                count= it.count
                 Log.e("count",it.count.toString())
                 Log.e("count2222222",count.toString())
             }
             .addOnFailureListener{
                 count= -2
             }
    return count
     }*/
// -------------------------------------------------------- permission -------------------------------------------------------//

    // -------------------------------------------------------- delete post -------------------------------------------------------//
    override suspend fun deleteGeneralPosts(postId: String, result: (Resource<String>) -> Unit) {
        val document = database.collection(FireStoreTable.post).document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deletePersonalPosts(
        postId: String,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID)
            .collection(FireStoreTable.post)

            .document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteCoursePosts(
        postId: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID)
            .collection(FireStoreTable.post).document(postId)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteSectionPosts(
        postId: String,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

// -------------------------------------------------------- delete post -------------------------------------------------------//


    // -------------------------------------------------------- add post -------------------------------------------------------//
    override suspend fun addGeneralPosts(posts: Posts, result: (Resource<String>) -> Unit) {
        val document = database.collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addPersonalPosts(
        posts: Posts,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID)
            .collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addCoursePosts(
        posts: Posts,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID)
            .collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addSectionPosts(
        posts: Posts,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document =
            database.collection(PostType.section_posts).document(dep).collection(section).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- add post -------------------------------------------------------//


    // -------------------------------------------------------- get post -------------------------------------------------------//
    override suspend fun getGeneralPosts(result: (Resource<List<Posts>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.post)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Posts>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Posts::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }

    //TODO() there is an error in the logic of the section posts need to be done
    override suspend fun getSectionPosts(
        section: String,
        dep: String,
        result: (Resource<List<Posts>>) -> Unit
    ) {
        val document = database.collection(PostType.section_posts).document(dep).collection(section)

        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Posts>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Posts::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }

    override suspend fun getCoursePosts(
        courses: List<Courses>,
        result: (Resource<List<Posts>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Posts>()
        for (course in courses) {
            val document =
                database.collection(FireStoreTable.courses).document(course.courseCode).collection(
                    FireStoreTable.post
                )

            document.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Posts::class.java)
                    Log.e("MMNNBB", post.postID)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))
    }

    override suspend fun getPersonalPosts(
        userID: String,
        grade: String,
        result: (Resource<List<Posts>>) -> Unit
    ) {

        val docRef = database.collection(grade)
            .whereEqualTo("code", userID)
        docRef.get()
            .addOnSuccessListener {
                lateinit var student: UserStudent
                for (rec in it) {
                    student = rec.toObject(UserStudent::class.java)
                }
                val document = database.collection(PostType.personal_posts).document(student.userId)
                    .collection(
                        FireStoreTable.post
                    )

                document.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        result.invoke(Resource.Failure(e.toString()))
                        return@addSnapshotListener
                    }

                    val listOfPosts = arrayListOf<Posts>()
                    for (rec in snapshot!!) {
                        val post = rec.toObject(Posts::class.java)
                        listOfPosts.add(post)
                    }
                    result.invoke(Resource.Success(listOfPosts))
                }

            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }


    }
// -------------------------------------------------------- get post -------------------------------------------------------//


    // -------------------------------------------------------- courses -------------------------------------------------------//
    override suspend fun updateCourse(
        courses: Courses,
        professor: Professor,
        assistant: Assistant,
        result: (Resource<String>) -> Unit
    ) {
        var str = ""
        val document = database.collection(FireStoreTable.courses).document(courses.courseCode)
        document.set(courses)
            .addOnSuccessListener {
                GlobalScope.launch {
                    str += "course data add, "
                    updateProfessor(professor, courses.courseCode) {
                        str += it
                    }
                    updateAssistant(assistant, courses.courseCode) {
                        str += it
                    }
                }

                result.invoke(
                    Resource.Success(str)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    // -------------------------------------------------------- teaching stuff -------------------------------------------------------//
    override suspend fun updateProfessor(
        professor: Professor,
        courseId: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseId)
            .collection(FireStoreTable.professor).document(professor.code)
        document.set(professor)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("professor added, ")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun updateAssistant(
        assistant: Assistant,
        courseId: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseId)
            .collection(FireStoreTable.assistant).document(assistant.code)
        document.set(assistant)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("assistant added, ")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    // -------------------------------------------------------- teaching stuff -------------------------------------------------------//
    override suspend fun getCourseByGrade(
        grade: String,
        result: (Resource<List<Courses>>) -> Unit
    ) {


        val docRef = database.collection(FireStoreTable.courses)
            .whereEqualTo("grade", grade)
        docRef.get()
            .addOnSuccessListener {
                val courses = arrayListOf<Courses>()
                for (rec in it) {
                    val course = rec.toObject(Courses::class.java)
                    courses.add(course)
                }
                result.invoke(
                    Resource.Success(courses)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun getAllCourses(result: (Resource<List<Courses>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses)

        docRef.get()
            .addOnSuccessListener {
                val courses = arrayListOf<Courses>()
                for (rec in it) {
                    val course = rec.toObject(Courses::class.java)
                    courses.add(course)
                }
                result.invoke(
                    Resource.Success(courses)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteCourse(courseID: String, result: (Resource<String>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses).document(courseID)

        docRef.delete()
            .addOnSuccessListener {

                result.invoke(
                    Resource.Success("deleted successfully ")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- courses -------------------------------------------------------//


    // -------------------------------------------------------- schedule -------------------------------------------------------//
    override suspend fun updateSection(
        section: Section,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(dep)
            .collection(section.section)
        section.sectionId = document.id
        document.add(section)
            .addOnSuccessListener {
                val query = database.collection(FireStoreTable.courses).document(section.courseCode)
                    .collection(FireStoreTable.sections)
                    .document(dep)
                    .collection(section.section)
                val countQuery = query.count()
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        if (snapshot.count < 2) {
                            val document2 = database.collection(FireStoreTable.assistant_sections)
                                .document(section.assistantID)
                                .collection(FireStoreTable.sections)
                            document2.add(SectionData(section.courseCode, dep, section.section))
                                .addOnSuccessListener {
                                    result.invoke(
                                        Resource.Success("sections added successfully")
                                    )
                                }
                                .addOnFailureListener {
                                    result.invoke(
                                        Resource.Failure(
                                            it.localizedMessage
                                        )
                                    )
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }


/*
    val document2=database.collection("sectionsForAdmin_${grade}")
    document.add("${section.courseCode}_${dep}_${section.section}")*/
    }

    override suspend fun updateLecture(
        lecture: Lecture,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures).document(dep).collection(dep)
        lecture.lectureId = document.id
        document.add(lecture)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("lectures added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }


    override suspend fun deleteSection(
        section: Section,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(dep)
            .collection(section.section)
            .document(section.sectionId)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("sections deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteLecture(
        lecture: Lecture,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures).document(dep).collection(dep)
            .document(lecture.lectureId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("lectures deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
// -------------------------------------------------------- schedule -------------------------------------------------------//


    // -------------------------------------------------------- update comment -------------------------------------------------------//
    override suspend fun updateCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment
        ).document(comment.commentID)
        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun updateCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {

        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun updateCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)
        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun updateCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- update comment -------------------------------------------------------//


    // -------------------------------------------------------- delete comment -------------------------------------------------------//
    override suspend fun deleteCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment
        ).document(comment.commentID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {

        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun deleteCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- delete comment -------------------------------------------------------//


    // -------------------------------------------------------- add comment -------------------------------------------------------//
    override suspend fun addCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment
        )
        comment.commentID = document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {

        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID = document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun addCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID = document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID = document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- add comment -------------------------------------------------------//


    // -------------------------------------------------------- get comment -------------------------------------------------------//
    override suspend fun getCommentGeneralPosts(
        postID: String,
        result: (Resource<List<Comment>>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)

        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Comment>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }

    }

    override suspend fun getCommentSectionPosts(
        postID: String,
        section: String,
        dep: String,
        result: (Resource<List<Comment>>) -> Unit
    ) {
        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Comment>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }

    override suspend fun getCommentCoursePosts(
        postID: String,
        courseID: String,
        result: (Resource<List<Comment>>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Comment>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }


    }

    override suspend fun getCommentPersonalPosts(
        postID: String,
        userID: String,
        result: (Resource<List<Comment>>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post
        )
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Comment>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }
// -------------------------------------------------------- get comment -------------------------------------------------------//


    // -------------------------------------------------------- get teaching data -------------------------------------------------------//
    override suspend fun getAllProfessors(result: (Resource<List<Professor>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.userTeaching)
            .whereEqualTo("userType", UserTypes.professorUser)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<Professor>()
                for (rec in it) {
                    val student = rec.toObject(Professor::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun getAllAssistants(result: (Resource<List<Assistant>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.userTeaching)
            .whereEqualTo("userType", UserTypes.assistantUser)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<Assistant>()
                for (rec in it) {
                    val student = rec.toObject(Assistant::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
// -------------------------------------------------------- get teaching data -------------------------------------------------------//

    /*
// for teaching
    override suspend fun getCourseByProfessorCode( professorCode:String,result: (Resource<List<Courses>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses)
            .whereEqualTo("professor", professorCode)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Courses>()
            for (rec in snapshot!!){
                val post = rec.toObject(Courses::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }
    override suspend fun getCourseByAssistantCode( assistantCode:String,result: (Resource<List<Courses>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses)
            .whereEqualTo("learningAssistant", assistantCode)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Courses>()
            for (rec in snapshot!!){
                val post = rec.toObject(Courses::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }
    override suspend fun getPosts(
        courses: List<Courses>,
        section: String,
        dep: String,
        userID: String,
        result: (Resource<List<Posts>>) -> Unit
    ) {
        val listOfPosts= arrayListOf<Posts>()
        for(course in courses){
            val document=database.collection(FireStoreTable.courses).document(course.courseCode).collection(
                FireStoreTable.post)

            document.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!){
                    val post = rec.toObject(Posts::class.java)
                    listOfPosts.add(post)
                }

            }    }

        val document=database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post)

        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            for (rec in snapshot!!){
                val post = rec.toObject(Posts::class.java)
                listOfPosts.add(post)
            }
        }

            val document2=database.collection(PostType.section_posts).document(dep).collection(section)

            document2.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }

                for (rec in snapshot!!){
                    val post = rec.toObject(Posts::class.java)
                    listOfPosts.add(post)
                }


    }

        val docRef = database.collection(FireStoreTable.post)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            for (rec in snapshot!!){
                val post = rec.toObject(Posts::class.java)
                listOfPosts.add(post)
            }
        }

        result.invoke(Resource.Success(listOfPosts))

    }
*/
    //TODO() make the function normal not snapshot
    override suspend fun getAssistant(
        courses: List<Courses>,
        result: (Resource<List<Assistant>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Assistant>()
        for (course in courses) {
            val docRef = database.collection(FireStoreTable.courses).document(course.courseCode)
                .collection(FireStoreTable.assistant)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Assistant::class.java)
                    Log.e("repo assistant", post.name)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))
    }

    override suspend fun getProfessor(
        courses: List<Courses>,
        result: (Resource<List<Professor>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Professor>()
        for (course in courses) {
            val docRef = database.collection(FireStoreTable.courses).document(course.courseCode)
                .collection(FireStoreTable.professor)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Professor::class.java)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))

    }

    /* override suspend fun getPermission(
         userId: String,
         result: (Resource<Permission?>) -> Unit
     ) {
         val docRef =  database.collection(PermissionsRequired.sing_in_permission)
             .document(userId)

         docRef.addSnapshotListener { snapshot, e ->
             if (e != null) {
                 result.invoke(Resource.Failure(e.toString()))
                 return@addSnapshotListener
             }
             result.invoke(Resource.Success(snapshot?.toObject(Permission::class.java)))
         }   }*/
    override suspend fun getSection(
        courses: List<Courses>,
        dep: String,
        section: String,
        result: (Resource<List<Section>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Section>()
        for (course in courses) {
            Log.e("repo", course.courseCode)
            val docRef =
                database.collection(FireStoreTable.courses)
                    .document(course.courseCode)
                    .collection(FireStoreTable.sections)
                    .document(dep)
                    .collection(section)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Section::class.java)
                    Log.e("i am here section", post.assistantName)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))

    }

    override fun getLectures(
        courses: List<Courses>,
        dep: String,
        result: (Resource<List<Lecture>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Lecture>()
        for (course in courses) {
            val docRef = database.collection(FireStoreTable.courses).document(course.courseCode)
                .collection(FireStoreTable.lectures)
                .document(dep).collection(dep)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Lecture::class.java)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))
    }

    override fun getLectures2(
        courses: List<Courses>,
        dep: String,
        result: (List<Lecture>?) -> Unit
    ) {
        val listOfPosts = arrayListOf<Lecture>()
        for (course in courses) {
            Log.e("i am here", course.courseCode)
            val docRef = database.collection(FireStoreTable.courses).document(course.courseCode)
                .collection(FireStoreTable.lectures)
                .document(dep).collection(dep)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(null)
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Lecture::class.java)
                    Log.e("i am here", post.professorName)
                    listOfPosts.add(post)
                }

            }


        }
        result.invoke(listOfPosts)


    }
}
