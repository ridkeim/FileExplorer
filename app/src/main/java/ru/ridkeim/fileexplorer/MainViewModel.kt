package ru.ridkeim.fileexplorer

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.ridkeim.fileexplorer.data.FileData
import java.io.File
import java.util.*

class MainViewModel : ViewModel() {
    @Suppress("DEPRECATION")
    private val rootFile : File = Environment.getExternalStorageDirectory()
    private val _currentDirectory = MutableLiveData(rootFile)
    val currentDirectory : LiveData<File>
        get() = _currentDirectory
    private val dStack = LinkedList<File>().apply {
        add(rootFile)
    }
    private val fileToFileDataFunction : ((File) -> FileData) = {
        val name = if(it != rootFile){
            "${it.name}/"
        } else {
            "/"
        }
        FileData(it.path,name,it.isDirectory)
    }
    private val _directoryStack = MutableLiveData(dStack.map(fileToFileDataFunction).toList())
    val directoryStack : LiveData<List<FileData>>
    get() = _directoryStack

    class Factory : ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private fun updateState(directory : File){
        if (directory != _currentDirectory.value){
            _directoryStack.postValue(dStack.map(fileToFileDataFunction).toList())
            _currentDirectory.postValue(directory)
        }
    }

    fun offerChildDirectory(directory : File){
        dStack.add(directory)
        updateState(directory)
    }

    fun popToExistingParent(){
        if(dStack.size > 1){
            do {
                dStack.removeLast()
            } while (dStack.size > 1 && dStack.last?.exists() != true)
            updateState(dStack.last)
        }
    }

    fun popToParentTill(file : File){
        while (dStack.size >1 && dStack.last != file){
            dStack.removeLast()
        }
        if (!dStack.last.exists()){
            popToExistingParent()
        }else{
            updateState(dStack.last)
        }
    }
}