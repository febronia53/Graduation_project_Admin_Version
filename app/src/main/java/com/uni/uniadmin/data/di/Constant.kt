package com.uni.uniadmin.data.di

object FireStoreTable {
    val post="posts"
    val comment="comment"
    val courses="courses"
    val lectures="lectures"
    val sections ="sections"
    val professor="professor"
    val assistant="assistan"
    val assistant_sections="assistant_sections"
    val user="Admin_user"
    val permissiont="permissiont"
    val userTeaching="_teaching_users"
}
object UserTypes{
    val assistantUser="assistant_user"
    val professorUser="professor_user"
}
object SharedPreferencesTable{
    val local_shared_preferences="local_shared_preferences"
    val user_session="user_session"
    val userType="user_type"

}
object PermissionsRequired{
    val sing_in_permission="permissions"

}
object grades{
    val firstGrade= "grade one"
    val secondGrade= "grade two"
    val thirdGrade= "grade three"
    val fourthGrade= "grade four"
}
object PostType{
    val personal_posts= "personal_posts"
    val section_posts= "section_posts"
    val course= "course"
    val general= "general"
}
object departement{
    val CS= "CS"
    val IS= "IS"
    val IT= "IT"
    val BIO= "BIO"
    val SOFT= "SOFT"
    val GROUP1= "GROUP1"
    val GROUP2= "GROUP2"
    val GROUP3= "GROUP3"
    val GROUP4= "GROUP4"
    val GROUP5= "GROUP5"

}


object Section{
    val S1= "S1"
    val S2= "S2"
    val S3= "S3"
    val S4= "S4"
    val S5= "S5"
    val S6= "S6"
    val S7= "S7"
    val S8= "S8"
}
object SignUpKey{
    const val MAIN_DATA="main_data"
    const val BACK_DATA="back_data"
    const val FROM_HOME_SCREEN="message_from_home"
}