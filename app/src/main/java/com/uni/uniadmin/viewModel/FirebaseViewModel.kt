package com.uni.uniadmin.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uni.uniadmin.classes.*
import com.uni.uniadmin.classes.user.UserStudent
import com.uni.uniadmin.data.FirebaseRepo
import com.uni.uniadmin.data.Resource
import com.uni.uniteaching.classes.user.UserAdmin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val  repository: FirebaseRepo
): ViewModel() {



    //-----------------------------------------------------------post-------------------------------------------------------------//

    private val _addPost= MutableStateFlow<Resource<String>?>(null)
    val addPost=_addPost.asStateFlow()


    fun addPostPersonal(posts: Posts, userId:String)= viewModelScope.launch{
        _addPost.value= Resource.Loading
        repository.addPersonalPosts (posts ,userId){
            _addPost.value=it

        }}
    fun addPostGeneral(posts: Posts)= viewModelScope.launch{
        _addPost.value= Resource.Loading
        repository.addGeneralPosts(posts ){
            _addPost.value=it

        }}
    fun addPostSection(posts: Posts,section:String,dep:String)= viewModelScope.launch{
        _addPost.value= Resource.Loading
        repository.addSectionPosts(posts,section,dep ){
            _addPost.value=it

        }}
    fun addPostCourse(posts: Posts,courseID:String)= viewModelScope.launch{
        _addPost.value= Resource.Loading
        repository.addCoursePosts(posts,courseID){
            _addPost.value=it

        }}
    //-----------------------------------------------------------post-------------------------------------------------------------//


    //-----------------------------------------------------------comment-------------------------------------------------------------//
    private val _addCommentGeneral= MutableStateFlow<Resource<String>?>(null)
    val addCommentGeneral=_addCommentGeneral.asStateFlow()
    fun addCommentsPersonal(comment: Comment, postID:String, userId:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.addCommentPersonalPosts (comment,postID ,userId){
            _addCommentGeneral.value=it

        }}
    fun addCommentsGeneral(comment: Comment, postID:String)= viewModelScope.launch{

        _addCommentGeneral.value= Resource.Loading
        repository.addCommentGeneralPosts (comment,postID ){
            _addCommentGeneral.value=it

        }}

    fun addCommentsSection(comment: Comment, postID:String, section:String, dep:String)= viewModelScope.launch{

        _addCommentGeneral.value= Resource.Loading
        repository.addCommentSectionPosts (comment,postID,section,dep ){
            _addCommentGeneral.value=it

        }}
    fun addCommentsCourse(comment: Comment, postID:String, courseID: String)= viewModelScope.launch{

        _addCommentGeneral.value= Resource.Loading
        repository.addCommentCoursePosts (comment,postID,courseID ){
            _addCommentGeneral.value=it

        }}


    fun updateCommentsPersonal(comment: Comment, postID:String, userId:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.updateCommentPersonalPosts (comment,postID ,userId){
            _addCommentGeneral.value=it
        }}
    fun updateCommentsGeneral(comment: Comment, postID:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.updateCommentGeneralPosts (comment,postID ){
            _addCommentGeneral.value=it
        }}
    fun updateCommentsSection(comment: Comment, postID:String, section:String, dep:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.updateCommentSectionPosts (comment,postID,section,dep ){
            _addCommentGeneral.value=it
        }}
    fun updateCommentsCourse(comment: Comment, postID:String, courseID: String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.updateCommentCoursePosts (comment,postID,courseID ){
            _addCommentGeneral.value=it
        }}
    fun deleteCommentsPersonal(comment: Comment, postID:String, userId:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.deleteCommentPersonalPosts (comment,postID ,userId){
            _addCommentGeneral.value=it
        }}
    fun deleteCommentsGeneral(comment: Comment, postID:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.deleteCommentGeneralPosts (comment,postID ){
            _addCommentGeneral.value=it
        }}
    fun deleteCommentsSection(comment: Comment, postID:String, section:String, dep:String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.deleteCommentSectionPosts (comment,postID,section,dep ){
            _addCommentGeneral.value=it
        }}
    fun deleteCommentsCourse(comment: Comment, postID:String, courseID: String)= viewModelScope.launch{
        _addCommentGeneral.value= Resource.Loading
        repository.deleteCommentCoursePosts (comment,postID,courseID ){
            _addCommentGeneral.value=it
        }}


    private val _getCommentGeneral= MutableStateFlow<Resource<List<Comment>>?>(null)
    val getCommentGeneral=_getCommentGeneral.asStateFlow()
    fun getCommentsPersonal(postID:String,userId:String)= viewModelScope.launch{

        _getCommentGeneral.value= Resource.Loading
        repository.getCommentPersonalPosts (postID ,userId){
            _getCommentGeneral.value=it

        }}
    fun getCommentsGeneral(postID:String)= viewModelScope.launch{

        _getCommentGeneral.value= Resource.Loading
        repository.getCommentGeneralPosts (postID ){
            _getCommentGeneral.value=it

        }}

    fun getCommentsSection(postID:String,section:String,dep:String)= viewModelScope.launch{

        _getCommentGeneral.value= Resource.Loading
        repository.getCommentSectionPosts (postID,section,dep ){
            _getCommentGeneral.value=it

        }}
    fun getCommentsCourse(postID:String,courseID: String)= viewModelScope.launch{

        _getCommentGeneral.value= Resource.Loading
        repository.getCommentCoursePosts (postID,courseID ){
            _getCommentGeneral.value=it

        }}



//-----------------------------------------------------------comment-------------------------------------------------------------//

    //-----------------------------------------------------------permission-------------------------------------------------------------//

    private val _getPermission= MutableStateFlow<Resource<List<PermissionItem>>?>(null)
    val getPermission=_getPermission.asStateFlow()

    fun getPermission(grade:String)= viewModelScope.launch{
        _getPermission.value= Resource.Loading
        repository.getPermission (grade){
            _getPermission.value=it
        }}
    fun getPermissionById(studentId: String)= viewModelScope.launch{
        _getPermission.value= Resource.Loading
        repository.getPermissionByUserId (studentId){
            _getPermission.value=it
        }}
    private val _addPermission= MutableStateFlow<Resource<String>?>(null)
    val addPermission=_addPermission.asStateFlow()

    fun addPermission(grade:String,permission:PermissionItem)= viewModelScope.launch{
        _addPermission.value= Resource.Loading
        repository.addPermission (grade, permission ){
            _addPermission.value=it
        }}

    fun deletePermission(permissionID:PermissionItem)= viewModelScope.launch{
        _addPermission.value= Resource.Loading
        repository.deletePermission (  permissionID){
            _addPermission.value=it
        }}


    //-----------------------------------------------------------permission-------------------------------------------------------------//


    //-----------------------------------------------------------schedule-------------------------------------------------------------//
    private val _addSection= MutableStateFlow<Resource<String>?>(Resource.Loading)
    val addSection=_addSection.asStateFlow()
    fun addSection(section: Section,dep:String)= viewModelScope.launch{
        _addSection.value= Resource.Loading
        repository.updateSection(section,dep) {
            _addSection.value=it
        }}


    private val _addLecture= MutableStateFlow<Resource<String>?>(Resource.Loading)
    val addLecture=_addLecture.asStateFlow()
    fun addLecture(lecture: Lecture,dep:String)= viewModelScope.launch{
        _addLecture.value= Resource.Loading
        repository.updateLecture(lecture,dep) {
            _addLecture.value=it
        }}




    private val _getSection= MutableStateFlow<Resource<List<Section>>?>(Resource.Loading)
    val getSection=_getSection.asStateFlow()
    private val _getLecture= MutableStateFlow<Resource<List<Lecture>>?>(null)
    val getLecture=_getLecture.asStateFlow()

    fun getLecture(courses:List<Courses>, dep:String){
        repository.getLectures(courses,dep) {
            _getLecture.value=it
        }}

    fun getSection(courses:List<Courses>, dep: String, section: String)= viewModelScope.launch{
        _getSection.value= Resource.Loading
        repository.getSection(courses,dep,section) {
            _getSection.value=it
        }}

    private val _deleteSection= MutableStateFlow<Resource<String>?>(null)
    val deleteSection=_deleteSection.asStateFlow()
    private val _deleteLecture= MutableStateFlow<Resource<String>?>(null)
    val deleteLecture=_deleteLecture.asStateFlow()

    fun deleteSection(section:Section,dep:String)=viewModelScope.launch{
        _deleteSection.value= Resource.Loading
        repository.deleteSection(section,dep) {
            _deleteSection.value=it
        }}

    fun deleteLecture(lecture: Lecture, dep:String)=viewModelScope.launch{
        _deleteSection.value= Resource.Loading
        repository.deleteLecture(lecture,dep) {
            _deleteSection.value=it
        }}


    //-----------------------------------------------------------schedule-------------------------------------------------------------//





// -------------------------------------------------------- get teaching data -------------------------------------------------------//

    private val _getAllProfessor= MutableStateFlow<Resource<List<Professor>>?>(Resource.Loading)
    val getAllProfessor=_getAllProfessor.asStateFlow()
    fun getAllProfessor()= viewModelScope.launch{
        _getAllProfessor.value= Resource.Loading
        repository.getAllProfessors() {
            _getAllProfessor.value=it
        }}

    private val _getAllAssistant= MutableStateFlow<Resource<List<Assistant>>?>(Resource.Loading)
    val getAllAssistant=_getAllAssistant.asStateFlow()
    fun getAllAssistant()= viewModelScope.launch{
        _getAllAssistant.value= Resource.Loading
        repository.getAllAssistants() {
            _getAllAssistant.value=it
        }}
// -------------------------------------------------------- get teaching data -------------------------------------------------------//



// -------------------------------------------------------- courses data -------------------------------------------------------//
    private val _getCoursesByGrade= MutableStateFlow<Resource<List<Courses>>?>(Resource.Loading)
    val getCoursesByGrade=_getCoursesByGrade.asStateFlow()
    fun getCoursesByGrade( grade:String)= viewModelScope.launch{
        _getCoursesByGrade.value= Resource.Loading
        repository.getCourseByGrade(grade) {
            _getCoursesByGrade.value=it
        }}

    private val _getAllCourses= MutableStateFlow<Resource<List<Courses>>?>(Resource.Loading)
    val getAllCourses=_getAllCourses.asStateFlow()
    fun getAllCourses()= viewModelScope.launch{
        _getAllCourses.value= Resource.Loading
        repository.getAllCourses() {
            _getAllCourses.value=it
        }}

    private val _deleteCourse= MutableStateFlow<Resource<String>?>(Resource.Loading)
    val deleteCourse=_deleteCourse.asStateFlow()
    fun deleteCourse( courseID:String)= viewModelScope.launch{
        _deleteCourse.value= Resource.Loading
        repository.deleteCourse(courseID) {
            _deleteCourse.value=it
        }}
    private val _addCourse= MutableStateFlow<Resource<String>?>(Resource.Loading)
    val addCourse=_addCourse.asStateFlow()
    fun addCourse( courses: Courses,professor: Professor,assistant: Assistant)= viewModelScope.launch{
        _addCourse.value= Resource.Loading
        repository.updateCourse(courses,professor,assistant) {
            _addCourse.value=it
        }}
// -------------------------------------------------------- courses data -------------------------------------------------------//






// -------------------------------------------------------- student data -------------------------------------------------------//

    private val _searchStudentByID= MutableStateFlow<Resource<UserStudent>?>(Resource.Loading)
    val searchStudentByID=_searchStudentByID.asStateFlow()
    fun searchStudentByID(grade: String,code:String)= viewModelScope.launch{
        _searchStudentByID.value= Resource.Loading
        repository.searchStudentByID(grade,code) {
            _searchStudentByID.value=it
        }}

    private val _searchStudentAll= MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentAll=_searchStudentAll.asStateFlow()
    fun searchStudentAll(grade: String)= viewModelScope.launch{
        _searchStudentAll.value= Resource.Loading
        repository.searchStudentAll(grade) {
            _searchStudentAll.value=it
        }}

    private val _searchStudentByDepartment= MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentByDepartment=_searchStudentByDepartment.asStateFlow()
    fun searchStudentByDepartment(grade: String,dep: String)= viewModelScope.launch{
        _searchStudentAll.value= Resource.Loading
        repository.searchStudentByDepartment(grade,dep) {
            _searchStudentAll.value=it
        }}

    private val _searchStudentBySection= MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentBySection=_searchStudentBySection.asStateFlow()
    fun searchStudentBySection(grade: String,dep: String,section: String)= viewModelScope.launch{
        _searchStudentAll.value= Resource.Loading
        repository.searchStudentBySection(grade,section,dep) {
            _searchStudentAll.value=it
        }}


// -------------------------------------------------------- student data -------------------------------------------------------//














    private val _getCourses= MutableStateFlow<Resource<List<Courses>>?>(null)
    val getCourses=_getCourses.asStateFlow()
    private val _getProfessor= MutableStateFlow<Resource<List<Professor>>?>(null)
    val getProfessor=_getProfessor.asStateFlow()
    private val _getAssistant= MutableStateFlow<Resource<List<Assistant>>?>(null)
    val getAssistant=_getAssistant.asStateFlow()

    //-----------------------------------------------------------------------------------------------------




    private val _getPosts= MutableStateFlow<Resource<List<Posts>>?>(null)
    val getPosts=_getPosts.asStateFlow()
    private val _getPostsCourses= MutableStateFlow<Resource<List<Posts>>?>(null)
    val getPostsCourses=_getPostsCourses.asStateFlow()

fun getPostsGeneral()= viewModelScope.launch{
    _getPosts.value= Resource.Loading
    repository.getGeneralPosts() {
        _getPosts.value=it
    }
}
    fun getPostsCourse(courses:List<Courses>)= viewModelScope.launch{
        _getPostsCourses.value= Resource.Loading
        repository.getCoursePosts(courses) {
            _getPostsCourses.value=it
        }
    }

    fun getPostsPersonal(studentId:String)= viewModelScope.launch{
        _getPosts.value= Resource.Loading
        repository.getPersonalPosts(studentId) {
            _getPosts.value=it
        }
    }
    fun getPostsSection(section:String,dep:String)= viewModelScope.launch{
        _getPosts.value= Resource.Loading
        repository.getSectionPosts(section,dep) {
            _getPosts.value=it
        }
    }



    fun getProfessor()= viewModelScope.launch{
        _getProfessor.value= Resource.Loading
        repository.getAllProfessors() {
            _getProfessor.value=it
        }}
    fun getAssistant()= viewModelScope.launch{
        _getAssistant.value= Resource.Loading
        repository.getAllAssistants() {
            _getAssistant.value=it
        }}



  /*  fun getStudentUser(grade:String,code:String,result: (UserStudent?) -> Unit){
        repository.searchStudentByID(grade,code,result)
    }*/

}
