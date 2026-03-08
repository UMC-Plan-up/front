package com.planup.planup.main.home.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.planup.planup.R
import com.planup.planup.databinding.FragmentPhotoManageBinding
import com.planup.planup.main.home.adapter.PhotoManageAdapter
import com.planup.planup.main.home.adapter.UploadItem
import dagger.hilt.android.AndroidEntryPoint
import com.planup.planup.main.home.ui.viewmodel.PhotoManageViewModel
import kotlinx.coroutines.launch
import java.io.File
import android.util.Base64
import android.util.Log
import com.planup.planup.network.ApiResult

@AndroidEntryPoint
class PhotoManageFragment : Fragment(R.layout.fragment_photo_manage) {

    private lateinit var binding: FragmentPhotoManageBinding

    private val viewModel: PhotoManageViewModel by viewModels()
    private var cameraImageUri: Uri? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.setImage(it)
                photoAdapter.addPhoto(it)
                uploadPhotos()
            }
        }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                viewModel.setImage(cameraImageUri!!)
                photoAdapter.addPhoto(cameraImageUri!!)
                uploadPhotos()
            }
        }

    private lateinit var photoAdapter: PhotoManageAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhotoManageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initAdapter()
        initRecyclerView()
        initClickListener()
        observeViewModel()

        viewModel.loadPhotos(createErrorHandler("loadPhotos") { photos ->
            photoAdapter.setPhotos(photos)
        })
    }

    // ---------------- RecyclerView ----------------

    private fun initRecyclerView() {
        binding.photoManageItemRv.layoutManager =
            GridLayoutManager(requireContext(), 3)

        binding.photoManageItemRv.adapter = photoAdapter
        photoAdapter.isSelectionMode = false
    }

    // ---------------- Adapter ----------------

    private fun initAdapter() {

        photoAdapter = PhotoManageAdapter(

            onAddClick = {
                openGallery()
            },

            onPhotoClick = { photo ->
                // 선택모드 아닐 때 사진 클릭
                Toast.makeText(
                    requireContext(),
                    "photo id: ${photo.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // ---------------- Click Listener ----------------

    private fun initClickListener() {
        binding.photoManageBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 삭제 버튼
        binding.photoManageDeleteTv.setOnClickListener {
            if(photoAdapter.isSelectionMode) {
                showImageDeleteBottomSheet()
            } else {
                photoAdapter.isSelectionMode = true

                Toast.makeText(
                    requireContext(),
                    "삭제할 사진을 선택하세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------- ViewModel Observe ----------------

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photoList.collect { photos ->
                    val list = mutableListOf<UploadItem>()
                    // 첫 칸 추가 버튼
                    list.add(UploadItem.AddButton)
                    photos.forEach { item ->
                        list.add(
                            UploadItem.PhotoItem(
                                id = item.verificationId,
                                imageUrl = item.photoImg
                            )
                        )
                    }
                    photoAdapter.submitList(list)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteResult.collect {
                    Toast.makeText(
                        requireContext(),
                        "삭제 완료",
                        Toast.LENGTH_SHORT
                    ).show()
                    photoAdapter.isSelectionMode = false
                    viewModel.loadPhotos(createErrorHandler("loadPhotos"){
                        photoAdapter.setPhotos(it)
                    })
                }
            }
        }
    }

    // ---------------- Gallery ----------------

    private fun openGallery() {
        showPhotoPopup(binding.photoManageItemRv)
    }

    private fun showPhotoPopup(anchorView: View) {
        // 1. 레이아웃 인플레이터로 뷰 객체 생성
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_photo_manage_add, null)

        // 2. PopupWindow 객체 생성 (가로, 세로 크기 지정)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // 외부 클릭 시 닫힘 설정
        )

        // 3. 내부 클릭 이벤트 처리
        popupView.findViewById<LinearLayout>(R.id.layout_gallery).setOnClickListener {
            // 갤러리 실행 로직
            galleryLauncher.launch("image/*")
            popupWindow.dismiss() // 클릭 후 팝업 닫기
        }

        popupView.findViewById<LinearLayout>(R.id.layout_camera).setOnClickListener {
            // 카메라 실행 로직
            cameraImageUri = createCameraImageUri()
            cameraLauncher.launch(cameraImageUri)
            popupWindow.dismiss()
        }

        // 4. 위치 설정 및 출력 (버튼의 바로 아래)
        // showAsDropDown(기준뷰, x오프셋, y오프셋)
        popupWindow.showAsDropDown(anchorView, 0, 10)
    }
    private fun showImageDeleteBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_photo_delete, null)
        view.findViewById<TextView>(R.id.photo_manage_delete_tv).setOnClickListener {
            photoAdapter.isSelectionMode = false
            val selectedPhotos = photoAdapter.getSelectedItems()
            if (selectedPhotos.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "삭제할 사진을 선택하세요",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val ids = selectedPhotos.map { it.id }
            viewModel.deletePhotos(ids)
        }

        view.findViewById<TextView>(R.id.photo_manage_cancel_tv).setOnClickListener {
            photoAdapter.isSelectionMode = false
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun createCameraImageUri(): Uri {
        val file = File(
            requireContext().cacheDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
    }

    fun uriToBase64(context: Context, uri: Uri): String {

        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun uploadPhotos() {
        val photoItems = photoAdapter.currentList
            .filterIsInstance<UploadItem.PhotoItem>()
        val base64List = photoItems.map {
            val uri = Uri.parse(it.imageUrl)
            uriToBase64(requireContext(), uri)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postPhotos(
                date = "2026-03-08", //TODO: 날짜 바꾸기
                photoList = base64List
            )
        }
    }

    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }
}