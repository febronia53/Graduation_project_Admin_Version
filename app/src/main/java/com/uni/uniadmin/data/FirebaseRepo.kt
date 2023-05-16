package com.uni.uniadmin.data

import com.uni.uniadmin.classes.*
import com.uni.uniadmin.classes.user.UserStudent


interface FirebaseRepo {

    suspend fun addPermission(grade:String,permission: PermissionItem, result: (Resource<String>) -> Unit)
    suspend fun deletePermission(grade:String,permissionID: String, result: (Resource<String>) -> Unit)
    suspend fun getPermission(grade:String, result: (Resource<List<PermissionItem>>) -> Unit)


     suspend fun deleteSection(section: Section,dep:String, result: (Resource<String>) -> Unit)
     suspend fun deleteLecture(lecture: Lecture,dep:String, result: (Resource<String>) -> Unit)

    suspend fun searchStudentBySection( grade:String,section:String,department:String,result: (Resource<List<UserStudent>>) -> Unit)
    suspend fun searchStudentByID( grade:String,code:String,result: (Resource<UserStudent>) -> Unit)
    suspend fun searchStudentByDepartment( grade:String,department: String,result: (Resource<List<UserStudent>>) -> Unit)
    suspend fun searchStudentAll( grade:String,result: (Resource<List<UserStudent>>) -> Unit)

    suspend fun updateCourse(courses: Courses,professor: Professor,assistant: Assistant, result: (Resource<String>) -> Unit)
    suspend fun updateProfessor(professor: Professor,courseId:String, result: (Resource<String>) -> Unit)
    suspend fun updateAssistant(assistant: Assistant,courseId:String, result: (Resource<String>) -> Unit)
    suspend fun getCourseByGrade(grade:String,result: (Resource<List<Courses>>) -> Unit)
    suspend fun getAllCourses(result: (Resource<List<Courses>>) -> Unit)
    suspend fun deleteCourse(courseID:String,result: (Resource<String>) -> Unit)

    suspend fun getAllProfessors( result: (Resource<List<Professor>>) -> Unit)
    suspend fun getAllAssistants( result: (Resource<List<Assistant>>) -> Unit)
    suspend fun addGeneralPosts(posts: Posts, result: (Resource<String>) -> Unit)
    suspend fun addPersonalPosts(posts: Posts, userID: String, result: (Resource<String>) -> Unit)
    suspend fun updateSection(section: Section,dep:String, result: (Resource<String>) -> Unit)
    suspend fun addCoursePosts(posts: Posts, courseID: String, result: (Resource<String>) -> Unit)
    suspend fun addSectionPosts(posts: Posts, section: String, dep: String, result: (Resource<String>) -> Unit)
    suspend fun updateLecture(lecture: Lecture,dep:String, result: (Resource<String>) -> Unit)
    suspend fun deleteCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,

        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    )
     suspend fun updateCommentGeneralPosts(
         comment: Comment,
         postID: String,
         result: (Resource<String>) -> Unit
    )
     suspend fun updateCommentSectionPosts(
         comment: Comment,
         postID: String,
         section: String,
         dep: String,

         result: (Resource<String>) -> Unit
    )
     suspend fun updateCommentCoursePosts(
         comment: Comment,
         postID: String,
         courseID: String,
         result: (Resource<String>) -> Unit
    )
     suspend fun updateCommentPersonalPosts(
         comment: Comment,
         postID: String,
         userID: String,
         result: (Resource<String>) -> Unit
    )
    suspend fun getAssistant(courses: List<Courses>, result: (Resource<List<Assistant>>) -> Unit)
    suspend fun getGeneralPosts( result: (Resource<List<Posts>>) -> Unit)
    suspend fun getSectionPosts( section:String,dep:String, result: (Resource<List<Posts>>) -> Unit)
    suspend fun getCoursePosts(courses: List<Courses>, result: (Resource<List<Posts>>) -> Unit)
    suspend fun getPersonalPosts(userID:String, result: (Resource<List<Posts>>) -> Unit)

  /*  suspend fun getPosts(
        courses: List<Courses>,
        section: String,
        dep: String,
        userID: String,
        result: (Resource<List<Posts>>) -> Unit
    )*/
    suspend fun addCommentGeneralPosts(comment: Comment, postID:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentSectionPosts(comment: Comment, postID:String, section:String, dep:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentCoursePosts(comment: Comment, postID:String, courseID:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentPersonalPosts(comment: Comment, postID:String, userID:String, result: (Resource<String>) -> Unit)



    suspend fun getCommentGeneralPosts( postID:String,result: (Resource<List<Comment>>) -> Unit)
    suspend fun getCommentSectionPosts(postID:String, section:String,dep:String, result: (Resource<List<Comment>>) -> Unit)
    suspend fun getCommentCoursePosts(postID:String,courseID:String, result: (Resource<List<Comment>>)-> Unit)
    suspend fun getCommentPersonalPosts(postID:String,userID:String, result:(Resource<List<Comment>>) -> Unit)





    suspend fun getProfessor(courses: List<Courses>, result: (Resource<List<Professor>>) -> Unit)
    //suspend fun getPermission(userId:String,result: (Resource<Permission?>) -> Unit)
    suspend fun getSection(courses: List<Courses>, dep :String, section:String, result: (Resource<List<Section>>) -> Unit)
    fun getLectures2(courses: List<Courses>, dep:String, result: (List<Lecture>?) -> Unit)
     fun getLectures(courses: List<Courses>, dep:String, result: (Resource<List<Lecture>>) -> Unit)
 //   suspend fun getCourse( grade: String,result: (Resource<List<Courses>>) -> Unit)
/*
    suspend fun getCourseByAssistantCode( assistantCode:String,result: (Resource<List<Courses>>) -> Unit)
    suspend fun  getCourseByProfessorCode( professorCode:String,result: (Resource<List<Courses>>) -> Unit)
     suspend fun updateSectionAttendance(attendance: Attendance, sectionId: String, result: (Resource<String>) -> Unit)
     suspend fun updateLectureAttendance(attendance: Attendance, lectureId: String, result: (Resource<String>) -> Unit)
*/

}