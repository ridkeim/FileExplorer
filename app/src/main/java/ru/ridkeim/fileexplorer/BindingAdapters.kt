package ru.ridkeim.fileexplorer

import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.ridkeim.fileexplorer.data.FileData

@BindingAdapter("fileSize")
fun TextView.fileSize(fileData : FileData){
    text = if(fileData.isDirectory){
        "(${fileData.subFiles} items)"
    }else{
        val size = fileData.size.toDouble() / (1024 * 1024)
        String.format("%.2f MB",size)
    }
}