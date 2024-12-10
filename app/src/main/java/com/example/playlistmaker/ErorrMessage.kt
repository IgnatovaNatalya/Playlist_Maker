package com.example.playlistmaker

enum class PlaceholderMessage(val  text:String, val image:Int) {

    MESSAGE_NO_INTERNET("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету", R.drawable.no_internet),
    MESSAGE_NOT_FOUND ("Ничего не нашлось", R.drawable.not_found),
    MESSAGE_CLEAR("",0);
}