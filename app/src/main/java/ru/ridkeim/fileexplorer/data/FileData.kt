package ru.ridkeim.fileexplorer.data

data class FileData(
    val path : String,
    val fileName : String,
    val isDirectory : Boolean,
    val size : Long = 0 ,
    val subFiles : Int = 0
    )
