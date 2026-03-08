package com.planup.planup.main.home.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.planup.planup.R
import com.planup.planup.main.goal.item.GoalPhotoResult

sealed class UploadItem {
    object AddButton : UploadItem()

    data class PhotoItem(
        val id: Int,
        val imageUrl: String,
        var isSelected: Boolean = false
    ) : UploadItem()
}

class PhotoManageAdapter(
    private val onAddClick: () -> Unit,
    private val onPhotoClick: (UploadItem.PhotoItem) -> Unit
) : ListAdapter<UploadItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_PHOTO = 1
    }

    var isSelectionMode = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // ---------------- ViewType ----------------

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UploadItem.AddButton -> TYPE_ADD
            is UploadItem.PhotoItem -> TYPE_PHOTO
        }
    }

    // ---------------- ViewHolder 생성 ----------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_ADD) {
            val view = inflater.inflate(R.layout.item_photo_manage_add, parent, false)
            AddViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_photo_manage, parent, false)
            PhotoViewHolder(view)
        }
    }

    // ---------------- Bind ----------------

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> holder.bind()
            is PhotoViewHolder -> holder.bind(getItem(position) as UploadItem.PhotoItem)
        }
    }

    // ---------------- Add ViewHolder ----------------

    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.setOnClickListener {
                onAddClick.invoke()
            }
        }
    }

    // ---------------- Photo ViewHolder ----------------

    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val image = view.findViewById<ImageView>(R.id.photo_manage_image_iv)
        private val checkIcon = view.findViewById<ImageView>(R.id.photo_manage_check_iv)

        fun bind(item: UploadItem.PhotoItem) {

            Glide.with(itemView.context)
                .load(item.imageUrl)
                .centerCrop()
                .into(image)

            if (isSelectionMode) {

                checkIcon.visibility = View.VISIBLE

                if (item.isSelected) {
                    checkIcon.setImageResource(R.drawable.icon_photo_checked)
                } else {
                    checkIcon.setImageResource(R.drawable.icon_photo_no_checked)
                }

            } else {

                checkIcon.visibility = View.GONE
            }

            itemView.setOnClickListener {

                if (isSelectionMode) {
                    toggleSelection(adapterPosition)
                } else {
                    onPhotoClick.invoke(item)
                }
            }
        }
    }

    // ---------------- 사진 추가 ----------------

    fun addPhoto(uri: Uri) {

        val list = currentList
            .filterIsInstance<UploadItem.PhotoItem>()
            .toMutableList()

        val newId = System.currentTimeMillis().toInt()

        list.add(
            UploadItem.PhotoItem(
                id = newId,
                imageUrl = uri.toString()
            )
        )

        submitList(buildListWithAddButton(list))
    }

    // ---------------- 여러 사진 추가 ----------------

    fun addPhotos(uris: List<Uri>) {

        val list = currentList
            .filterIsInstance<UploadItem.PhotoItem>()
            .toMutableList()

        uris.forEach {
            list.add(
                UploadItem.PhotoItem(
                    id = System.currentTimeMillis().toInt(),
                    imageUrl = it.toString()
                )
            )
        }

        submitList(buildListWithAddButton(list))
    }

    // ---------------- 리스트 초기 세팅 ----------------

    fun setPhotos(photoList: List<GoalPhotoResult>) {

        val list = photoList.mapIndexed { index, url ->
            UploadItem.PhotoItem(
                id = index,
                imageUrl = url.photoImg
            )
        }

        submitList(buildListWithAddButton(list))
    }

    // ---------------- AddButton 포함 리스트 생성 ----------------

    private fun buildListWithAddButton(
        photos: List<UploadItem.PhotoItem>
    ): List<UploadItem> {

        val list = mutableListOf<UploadItem>()

        list.add(UploadItem.AddButton)
        list.addAll(photos)

        return list
    }

    // ---------------- 선택 토글 ----------------

    private fun toggleSelection(position: Int) {

        val list = currentList.toMutableList()
        val item = list[position]

        if (item is UploadItem.PhotoItem) {
            list[position] = item.copy(isSelected = !item.isSelected)
        }

        submitList(list)
    }

    // ---------------- 선택된 아이템 ----------------

    fun getSelectedItems(): List<UploadItem.PhotoItem> {

        return currentList
            .filterIsInstance<UploadItem.PhotoItem>()
            .filter { it.isSelected }
    }

    // ---------------- DiffUtil ----------------

    class DiffCallback : DiffUtil.ItemCallback<UploadItem>() {

        override fun areItemsTheSame(
            oldItem: UploadItem,
            newItem: UploadItem
        ): Boolean {

            return when {
                oldItem is UploadItem.AddButton && newItem is UploadItem.AddButton -> true
                oldItem is UploadItem.PhotoItem && newItem is UploadItem.PhotoItem ->
                    oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: UploadItem,
            newItem: UploadItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}