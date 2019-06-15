package com.feedbacktower.fragments


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentHomeBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.ui.VideoTrimmerScreen
import com.feedbacktower.ui.videoplayer.VideoPlayerScreen
import com.feedbacktower.util.*
import com.feedbacktower.utilities.Glide4Engine
import com.feedbacktower.utilities.cropper.CropImage
import com.feedbacktower.utilities.cropper.ImagePreviewActivity
import com.feedbacktower.utilities.filepicker.FilePickerBuilder
import com.feedbacktower.utilities.filepicker.FilePickerConst
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import org.jetbrains.anko.toast
import java.io.File


class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: PostListAdapter
    private var isLoading: Boolean? = false
    private var currentCity: String? = null
    private var noPosts: Boolean? = false
    private val REQUEST_CODE_CHOOSE_IMAGE = 1012
    private val REQUEST_CODE_CHOOSE_VIDEO = 1013

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.change_city_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.select_city_id) {
            HomeFragmentDirections.actionNavigationHomeToSelectCityFragment().let {
                findNavController().navigate(it)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentHomeBinding) {
        // binding.toolbar.title = getString(R.string.app_name)
        binding.addPostListener = addPostClickListener
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        val appPrefs by lazy { AppPrefs.getInstance(requireContext()) }
        binding.isBusiness = appPrefs.user?.userType == "BUSINESS"
        binding.currentCity = appPrefs.getValue("CITY") ?: "Select City"
        //setup list
        feedListView.setVertical(requireContext())
        postAdapter = PostListAdapter(requireActivity(), listener)
        feedListView.adapter = postAdapter
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        swipeRefresh.setOnRefreshListener {
            fetchPostList()
        }

        binding.selectCityListener = View.OnClickListener {
            val dir = HomeFragmentDirections.actionNavigationHomeToSelectCityFragment()
            dir.onboarding = false
            findNavController().navigate(dir)
        }

    }

    override fun onResume() {
        super.onResume()
        fetchPostList()
    }

    private val addPostClickListener = View.OnClickListener {
        with(PermissionManager.getInstance()) {
            if (cameraPermissionGranted(requireContext())
                && writeStoragePermissionGranted(requireContext())
            ) {
                showPostDialog()
            } else {
                requestMediaPermission(requireActivity())
            }
        }
    }

    private fun showPostDialog() {
        AlertDialog.Builder(requireContext())
            .setItems(arrayOf("Text", "Photo", "Video")) { dialog, which ->
                when (which) {
                    0 -> requireActivity().launchActivity<PostTextScreen>()
                    1 -> pickImage()
                    2 -> pickVideo()
                }
            }.show()
    }

    private fun pickVideo() {
        Matisse.from(this)
            .choose(MimeType.ofVideo())
            .countable(true)
            .maxSelectable(1)
            .gridExpectedSize(resources.getDimensionPixelSize(com.feedbacktower.R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(Glide4Engine())
            .forResult(REQUEST_CODE_CHOOSE_VIDEO)
    }

    private fun pickImage() {

        FilePickerBuilder.instance.setMaxCount(1)
            .setActivityTheme(R.style.AppTheme_NoActionBar)
            .pickPhoto(this)
    }

    private val listener = object : PostListAdapter.Listener {
        override fun onLikeClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }

        override fun onVideoClick(item: Post, position: Int) {
            requireActivity().launchActivity<VideoPlayerScreen> {
                putExtra(VideoPlayerScreen.URI_KEY, Constants.Service.Secrets.BASE_URL + "/posts/${item.media}")
            }
        }
    }


    private fun likeUnlikePost(item: Post, position: Int) {
        PostManager.getInstance()
            .likePost(item.id) { response, _ ->
                if (response?.liked != null)
                    postAdapter.updateLike(position, response.liked)
            }
    }

    private fun fetchPostList() {
        swipeRefresh.isRefreshing = true
        PostManager.getInstance()
            .getPosts("", "OLD") { response, error ->
                swipeRefresh.isRefreshing = false
                noPosts = response?.posts?.isEmpty()
                postAdapter.submitList(response?.posts)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            var mSelected = Matisse.obtainResult(data!!)
            Log.d("Matisse", "mSelected Image: $mSelected")
            if (mSelected.size < 1) {
                requireContext().toast("No image selected")
                return
            }
            CropImage.activity(mSelected[0]).start(requireActivity())
        } else if (requestCode == REQUEST_CODE_CHOOSE_VIDEO && resultCode == RESULT_OK) {
            var mSelected = Matisse.obtainResult(data!!)
            if (mSelected.size < 1) {
                requireContext().toast("No video selected")
                return
            }
            Log.d("Matisse", "mSelected Video: $mSelected")
            requireActivity().launchActivity<VideoTrimmerScreen> {
                putExtra("URI", mSelected[0])
            }
        } else if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var paths: ArrayList<String> = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                if (paths.size < 1) {
                    requireContext().toast("No image selected")
                    return
                }
                Log.d(TAG, "Uri: ${File(paths[0]).toUri()}")
                //ImageEditHelper.openCropper(requireContext(), this, File(paths[0]).toUri())
                //requireActivity().launchActivity<CropImageActivity> {  }
                    requireActivity().launchActivity<ImagePreviewActivity> {
                        putExtra("URI", File(paths[0]).toUri())
                    }
                //openImageCropper(File(paths[0]).toUri())
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.d(TAG, "Crop result")
            data?.let {

                val uri = UCrop.getOutput(it)
                Log.d(TAG, "Crop result Uri: $uri")
                requireActivity().launchActivity<ImagePreviewActivity> {
                    putExtra("URI", uri)
                }
            }
        }
    }

    private fun openImageCropper(uri: Uri) {
        val path: File = requireContext().filesDir
        UCrop.of(uri, path.toUri())
            .start(requireContext(), this, UCrop.REQUEST_CROP)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            var allGranted = true
            grantResults.forEach {
                allGranted = it == PermissionChecker.PERMISSION_GRANTED
                if (!allGranted) {
                    requireContext().toast("You need to grant permission to add a post")
                    return
                }
            }
            if (allGranted)
                showPostDialog()
        }
    }
}
