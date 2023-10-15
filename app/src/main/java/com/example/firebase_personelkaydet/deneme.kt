package com.example.firebase_personelkaydet


open class Employee(
    val name:String = "",
    val surname:String = ""
)
open class Manager(
    val namea:String = "",
    val surnamea:String = ""
):Employee()


interface toDo{
    fun doJob(){
        println("ı am doing my job")
    }
}

object Settings:Manager(),toDo {
    override fun doJob() {
        println("batuuss")
    }

    var theme: String = "Light"
    var language: String = "English"
}



fun main() {
    // Singleton "Settings" sınıfından bir örneği alalım
    val settings = Settings

    // Özelliklere erişelim
    println("Theme: ${settings.theme}")
    println("Language: ${settings.language}")

}
