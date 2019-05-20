package com.feedbacktower.fragments


import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentHomeBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical
import java.text.FieldPosition
import com.zhihu.matisse.engine.impl.GlideEngine
import android.content.pm.ActivityInfo
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import android.content.Intent
import android.util.Log
import androidx.core.content.PermissionChecker
import androidx.navigation.fragment.findNavController
import com.feedbacktower.ui.VideoTrimmerScreen
import com.feedbacktower.util.PERMISSION_CODE
import com.feedbacktower.util.PermissionManager
import com.feedbacktower.utilities.Glide4Engine
import com.feedbacktower.utilities.cropper.ImagePreviewActivity
import org.jetbrains.anko.toast


class HomeFragment : Fragment() {

    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: PostListAdapter
    private var isLoading: Boolean? = false
    private var currentCity: String? = null
    private var noPosts: Boolean? = false
    private val REQUEST_CODE_CHOOSE_IMAGE = 1012
    private val REQUEST_CODE_CHOOSE_VIDEO = 1013
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
        binding.toolbar.title = getString(R.string.app_name)
        binding.addPostListener = addPostClickListener
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        val appPrefs by lazy { AppPrefs.getInstance(requireContext()) }
        binding.isBusiness = appPrefs.user?.userType == "BUSINESS"
        binding.currentCity = appPrefs.getValue("CITY")?:"Select City"
        //setup list
        feedListView.setVertical(requireContext())
        postAdapter = PostListAdapter(likeListener)
        feedListView.adapter = postAdapter
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        swipeRefresh.setOnRefreshListener {
            fetchPostList()
        }

        binding.selectCityListener =View.OnClickListener {
            val dir = HomeFragmentDirections.actionNavigationHomeToSelectCityFragment3()
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
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .gridExpectedSize(resources.getDimensionPixelSize(com.feedbacktower.R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(Glide4Engine())
            .forResult(REQUEST_CODE_CHOOSE_IMAGE)
    }

    private val likeListener = object : PostListAdapter.LikeListener {
        override fun onClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
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
            requireActivity().launchActivity<ImagePreviewActivity> {
                putExtra("URI", mSelected[0])
            }
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
        }
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
