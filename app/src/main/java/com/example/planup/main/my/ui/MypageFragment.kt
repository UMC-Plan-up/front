package com.example.planup.main.my.ui

import android.Manifest
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.my.adapter.ProfileImageAdapter
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.network.controller.UserController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MypageFragment : Fragment(), ServiceAlertAdapter, ProfileImageAdapter {
    lateinit var binding: FragmentMypageBinding

    //API 연동
    private lateinit var service: UserController

    //sharedPreferences
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    //카메라로 촬영한 이미지 파일의 uri 주소
    private var cameraImageUri: Uri? = null

    //앨범 접근 권한 팝업에서 권한을 설정한 이후 콜백
    private val albumPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        //안드로이드 14 이상인 경우
        //선택사진 또는 전체사진 중 한 개 권한만 허용해도 됨
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            //둘 중 하나의 권한이 허용된 경우 앨범 실행
            if (permissions[READ_MEDIA_VISUAL_USER_SELECTED]!! || permissions[Manifest.permission.READ_MEDIA_IMAGES]!!) {
                openAlbum()
            } else {
                //두 권한 모두 허용되지 않은 경우 토스트 메시지
                Toast.makeText(context as MainActivity, "앨범 접근 권한이 필요합니다.", LENGTH_SHORT).show()
            }
        } else {
            //안드로이드 14 이하인 경우
            //전체사진 접근 권한이 허용되어야 함
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                //권한이 허용된 경우 앨범 실행
                openAlbum()
            } else {
                //허용되지 않은 경우 토스트 메시지
                Toast.makeText(context as MainActivity, "앨범 접근 권한이 필요합니다.", LENGTH_SHORT).show()
            }
        }
    }
    //카메라 권한 설정에 대한 콜백 변수
    //카메라 실행 또는 토스트 메시지
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            //허용되지 않은 경우 토스트 메시지
            Toast.makeText(context as MainActivity, "카메라 접근 권한이 필요합니다.", LENGTH_SHORT).show()
        }
    }

    //파일 권한 설정에 대한 콜백 변수
    //파일 열기 또는 토스트 메시지
    private val filePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openFile()
        } else {
            //허용되지 않은 경우 토스트 메시지
            Toast.makeText(context as MainActivity, "파일 접근 권한이 필요합니다.", LENGTH_SHORT).show()
        }
    }
    //앨범에서 선택한 사진을 처리하는 콜백 변수
    private val albumLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            val file = uriToFile(context as MainActivity,uri!!)

            file?.let {
                val requestFile = it.asRequestBody("image/png".toMediaTypeOrNull())
                val multipartBody =
                    MultipartBody.Part.createFormData("file", it.name, requestFile)

                service.imageUploadService(multipartBody)
            }
        }
    }
    //카메라 실행 이후 콜백 변수
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {  result ->
        if (result.resultCode == RESULT_OK && cameraImageUri != null) {
            val file = uriToFile(context as MainActivity, cameraImageUri!!)

            file?.let {
                val requestFile = it.asRequestBody("image/png".toMediaTypeOrNull())
                val multipartBody =
                    MultipartBody.Part.createFormData("file", it.name, requestFile)

                service.imageUploadService(multipartBody)
            }
        }

    }

    //파일 접근 이후 콜백 변수
    private val fileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val fileImageUri = result.data?.data
        if(result.resultCode == RESULT_OK) {
            val file = uriToFile(context as MainActivity, fileImageUri!!)

            file?.let {
                val requestFile = it.asRequestBody("image/png".toMediaTypeOrNull())
                val multipartBody =
                    MultipartBody.Part.createFormData("file", it.name, requestFile)

                service.imageUploadService(multipartBody)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyPageNavView()
            }
        }
//        binding = FragmentMypageBinding.inflate(inflater, container, false)
//        init()
//        clickListener()
//        return binding.root
    }

    private fun init() {
        binding.mypageCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.mypageCl.height
                binding.mypageInnerCl.minHeight = height
                binding.mypageCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        //API 서비스
        service = UserController()
        service.setServiceAdapter(this)
        service.setProfileImageAdapter(this)
        //유저 정보 업데이트 및 UI에 반영
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()
        binding.mypageMainEmailTv.text = prefs.getString("email", "null").toString()
        //사용자 프로필 사진
        Glide.with(context as MainActivity)
            .load(prefs.getString("profileImg","no-data"))
            .error(ContextCompat.getDrawable(context,R.color.red_300)) //디버깅용 or 오류 이미지일 때 추가해도 될듯
            .into(binding.mypageMainImageIv)
    }

    private fun clickListener() {

//        binding.mypageBackIv.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, HomeFragment())
//                .commitAllowingStateLoss()
//        }

        binding.mypageMainImageCv.setOnClickListener {
            val intent = Intent(context as MainActivity, GoalActivity::class.java)
            startActivity(intent)
        }

        /*프로필 사진 변경*/
        binding.mypageMainRewriteIv.setOnClickListener {
            showDropdown(binding.mypageMainRewriteIv)
        }

        /*닉네임 변경*/
        binding.mypageNicknameIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageNicknameFragment())
                .commitAllowingStateLoss()
        }
        /*이메일 변경*/
        binding.mypageEmailIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailCheckFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 변경*/
        binding.mypagePasswordIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordEmailFragment())
                .commitAllowingStateLoss()
        }
        /*카카오톡 계정 연동*/
        binding.mypageKakaoIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageKakaoFragment())
                .commitAllowingStateLoss()
        }
        //기타 계정 관리
        binding.mypageOtherIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageOtherFragment())
                .commitAllowingStateLoss()
        }
        //차단 친구 관리
        binding.mypageFriendBlockIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFriendBlockFragment())
                .commitAllowingStateLoss()
        }

        //서비스 알림 수신 토글 끄기
        binding.mypageAlertServiceOnIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility = View.GONE
            binding.mypageAlertServiceOffIv.visibility = View.VISIBLE
        }
        //서비스 알림 수신 토글 켜기
        binding.mypageAlertServiceOffIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility = View.VISIBLE
            binding.mypageAlertServiceOffIv.visibility = View.GONE
        }
        //마케팅 알림 수신 토글 끄기
        binding.mypageAlertBenefitOnIv.setOnClickListener {
            service.notificationAgreementService(false)
        }
        //마케팅 알림 수신 토글 켜기
        binding.mypageAlertBenefitOffIv.setOnClickListener {
            service.notificationAgreementService(true)
        }
        //이용약관 및 정책
        binding.mypagePolicyIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePolicyFragment())
                .commitAllowingStateLoss()
        }


    }

    /*프로필 사진 재설정 드롭다운 메뉴*/
    private fun showDropdown(view: View) {

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dropdown_profile_img, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // 포커스 가능
        )

        // 팝업 바깥 클릭 시 닫힘 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context as MainActivity,R.color.transparent))

        // 팝업 표시 (예: 이미지뷰 아래에)
        popupWindow.showAsDropDown(view)

        popupView.findViewById<View>(R.id.album_cl).setOnClickListener {
            popupWindow.dismiss()
            accessAlbum()
        }
        popupView.findViewById<View>(R.id.photo_cl).setOnClickListener {
            popupWindow.dismiss()
            accessCamera()
        }
        popupView.findViewById<View>(R.id.file_cl).setOnClickListener {
            popupWindow.dismiss()
            accessFile()
        }


    }

    //앨범 접근 권한 설정 또는 앨범 실행
    private fun accessAlbum() {
        //버전에 따른 권한 확인
        //upside_down_cake 이상: 선택된 사진만 접근 허용 + 사진 접근 허용
        //tiramisu 이상: 사진 접근 허용
        //나머지: 저장소 읽기 허용
        val permissionList = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.READ_MEDIA_IMAGES)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val denied = permissionList.filter {
            //permissionList의 원소(권한)이 granted가 아닌 경우에만 denied에 저장
            //denied가 empty이면 모든 권한이 granted 상태라는 뜻
            ContextCompat.checkSelfPermission(context as MainActivity, it) != PackageManager.PERMISSION_GRANTED
        }
        if (denied.isNotEmpty()) {
            //안드로이드 14 이상인 경우 하나의 권한만 허용해도 앨범 접근
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && denied.size < 2) {
                openAlbum()
            } else {
                //granted 아닌 권한에 대해 허용을 요청하는 팝업 출력
                albumPermissionLauncher.launch(denied.toTypedArray())
            }
        } else {
            // 모두 허용됐으면 바로 앨범 접근
            openAlbum()
        }
    }

    //카메라 권한 설정 또는 카메라 열기
    private fun accessCamera(){
        val permission = arrayOf(Manifest.permission.CAMERA)
        val denied = permission.filter {
            ContextCompat.checkSelfPermission(context as MainActivity,it) != PackageManager.PERMISSION_GRANTED
        }
        if (denied.isNotEmpty()) {
            cameraPermissionLauncher.launch(denied[0])
        } else {
            openCamera()
        }
    }

    //파일 권한 설정 또는 파일 열기
    private fun accessFile(){

        var permission: Array<String>
        // SDK 버전에 따라 요청 권한 다르게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            permission = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val denied = permission.filter {
            ContextCompat.checkSelfPermission(context as MainActivity, it) != PackageManager.PERMISSION_GRANTED
        }
        if (denied.isNotEmpty()){
            filePermissionLauncher.launch(denied[0])
        } else {
            openFile()
        }
    }

    //앨범 열기
    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }
        albumLauncher.launch(intent)
    }

    //카메라 열기
    private fun openCamera(){
        val photoFile = createImageFile(context as MainActivity)
        cameraImageUri = FileProvider.getUriForFile(
            context as MainActivity,
            "com.example.planut.provider",
            photoFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        }
        cameraLauncher.launch(intent)
    }

    //파일 열기
    private fun openFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"  // png 파일만 표시
        }
        fileLauncher.launch(intent)
    }

    //마케팅 수신 동의하는 경우 팝업 메시지
    private fun alertAgreementPopup(view: Int) {
        val dialog = Dialog(context as MainActivity)
        val today = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
        dialog.setContentView(view)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
        //팝업 바깥 부분 터치하는 경우 팝업 끄기
        dialog.setCanceledOnTouchOutside(true)
        //닉네임, 오늘 날짜 출력하기
        dialog.findViewById<TextView>(R.id.popup_benefit_explain_tv).text = getString(
            R.string.popup_benefit_explain,
            prefs.getString("nickname", "null"),
            today
        )
        //확인버틍으로 팝업 끄기
        dialog.findViewById<TextView>(R.id.popup_benefit_ok_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //API 오류에 대한 토스트 메시지 출력
    private fun errorToast(message: String){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }

    //마케팅 수신 동의 API 성공
    override fun successServiceSetting(condition: Boolean) {
        if (condition) { //condition==true: 토글 켜기
            binding.mypageAlertBenefitOnIv.visibility = View.VISIBLE
            binding.mypageAlertBenefitOffIv.visibility = View.GONE
            alertAgreementPopup(R.layout.popup_benefit_agree)
        } else { //condition == false: 토글 끄기
            binding.mypageAlertBenefitOnIv.visibility = View.GONE
            binding.mypageAlertBenefitOffIv.visibility = View.VISIBLE
        }
    }

    //마케팅 수신 동의 API 오류
    override fun failServiceSetting(message: String) {
       errorToast(message)
    }

    //프로필 이미지 API 성공
    override fun successProfileImage(image: String) {
        //사용자 프로필 사진
        Glide.with(context as MainActivity).load(prefs.getString("profileImg","no-data")).into(binding.mypageMainImageIv)
    }

    //프로필 이미지 API 오류
    override fun failProfileImage(message: String) {
        errorToast(message)
    }

    //uri를 파일 형식으로 전환
    private fun uriToFile(context: Context, uri: Uri): File?{
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val file = createImageFile(context)
            copyInputStreamToFile(it, file)
            return file
        }
        return null
    }
    //이미지 파일 생성
    private fun createImageFile(context: Context): File {
        val timeStamp = System.currentTimeMillis()
        val imageFileName = "@${timeStamp}_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,
            ".png",
            storageDir
        )
    }
    //생성한 파일을 바이트 스트림으로 전환
    private fun copyInputStreamToFile(inputStream: InputStream,file: File){
        try {
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also {read = it} != -1) {
                   outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
