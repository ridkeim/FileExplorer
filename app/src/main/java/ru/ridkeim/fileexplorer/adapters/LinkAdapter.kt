package ru.ridkeim.fileexplorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ridkeim.fileexplorer.data.FileData
import ru.ridkeim.fileexplorer.databinding.LinkItemBinding

class LinkAdapter(private var data : List<FileData> = emptyList(), private val clickCallback: ((FileData) -> Unit)? = null) : RecyclerView.Adapter<LinkAdapter.ViewHolder>(){

    class ViewHolder private constructor(private val itemBinding : LinkItemBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(fileData: FileData, clickCallback: ((FileData) -> Unit)?) {
            itemBinding.item = fileData
            itemBinding.root.setOnClickListener {
                clickCallback?.let {
                    it(fileData)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val itemBinding = LinkItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
                return ViewHolder(itemBinding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], clickCallback)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun submitData(list: List<FileData>) {
        data = list
        notifyDataSetChanged()
    }

}