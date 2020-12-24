package ru.ridkeim.fileexplorer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ridkeim.fileexplorer.adapters.FilesAdapter
import ru.ridkeim.fileexplorer.data.FileData
import ru.ridkeim.fileexplorer.databinding.FragmentFilesBinding
import java.io.File
import java.util.*

class FilesFragment : Fragment() {
    private lateinit var filesAdapter: FilesAdapter
    private lateinit var vBinding: FragmentFilesBinding
    private val path by lazy {
        arguments?.getString(ARG_PATH) ?: ""
    }
    private val viewModel by activityViewModels<MainViewModel> {
        MainViewModel.Factory()
    }
    companion object {
        private const val ARG_PATH: String = "ru.ridkeim.fileexplorer.path"
        private val TAG = FilesFragment::class.qualifiedName
        fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var path: String = ""
        fun build(): FilesFragment {
            val fragment = FilesFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (path.isEmpty()) {
            Toast.makeText(context, "Path should not be null!", Toast.LENGTH_SHORT).show()
            return
        }else{
            Log.d(TAG,"fragment with $path is created")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        vBinding = FragmentFilesBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vBinding.fileRecycler.layoutManager = LinearLayoutManager(context)
        filesAdapter = FilesAdapter() {
            val file = File(it.path)
            if(it.isDirectory){
                viewModel.offerChildDirectory(file)
            }else{
                AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setTitle(file.name)
                        .setMessage(
                                "${file.absolutePath}\n${Date(file.lastModified())}\n${file.toURI()}"
                        )
                        .setPositiveButton("Закрыть"){dialog,_->
                            dialog.dismiss()
                        }
                        .show()
            }
        }
        vBinding.fileRecycler.adapter = filesAdapter
        updateData()
        Log.d(TAG,"view of fragment with $path is created")
    }

    private fun updateData() {
        val file = File(path)
        val filesList = file.listFiles()
        val data = filesList?.map {
            FileData(it.path, it.name, it.isDirectory, it.length(), it.list()?.size ?: 0)
        }?.toList() ?: emptyList()
        filesAdapter.submitData(data)
        vBinding.emptyMessage.visibility = when (data.size){
            0 -> View.VISIBLE
            else -> View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"fragment with $path is destroyed")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"fragment with $path is attached")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"view of fragment with $path is destroyed")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"fragment with $path is detached")
    }
}