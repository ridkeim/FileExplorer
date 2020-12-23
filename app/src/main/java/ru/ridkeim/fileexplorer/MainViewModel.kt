package ru.ridkeim.fileexplorer

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File

class MainViewModel : ViewModel() {

    private val rootFile : File = Environment.getExternalStorageDirectory()
    private val _currentDirectory = MutableLiveData(rootFile)
    val currentDirectory : LiveData<File>
        get() = _currentDirectory

    class Factory : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun setCurrentDirectory(directory : File){
        _currentDirectory.postValue(directory)
    }

    fun popToParent(){
        var cDir = currentDirectory.value ?: rootFile
        if(cDir == rootFile) {
            return
        }
        while (cDir.parentFile?.exists() != true){
            cDir = cDir.parentFile?:rootFile
        }
        _currentDirectory.postValue(cDir.parentFile)
    }

}