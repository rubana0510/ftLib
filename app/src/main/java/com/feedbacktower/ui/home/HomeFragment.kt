package com.feedbacktower.ui.home


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.feedbacktower.R
import com.feedbacktower.adapters.AdsPagerAdapter
import com.feedbacktower.adapters.AdsPagerAdapter2
import com.feedbacktower.adapters.DotAdapter
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.callbacks.OnPageChangeListener
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.db.AppDatabase
import com.feedbacktower.data.models.Ad
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentHomeBinding
import com.feedbacktower.fragments.UploadPostDialog
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetAdsResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.ui.ImagePreviewActivity
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.ui.VideoTrimmerScreen
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.videoplayer.VideoPlayerScreen
import com.feedbacktower.util.*
import com.feedbacktower.utilities.Glide4Engine
import com.feedbacktower.utilities.filepicker.FilePickerBuilder
import com.feedbacktower.utilities.filepicker.FilePickerConst
import com.feedbacktower.views.ViewPagerX
import com.theartofdev.edmodo.cropper.CropImage
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.File


class HomeFragment : BaseViewFragmentImpl(), HomeContract.View {
    private val TAG = "HomeFragment"
    private lateinit var presenter: HomePresenter
    private lateinit var feedListView: RecyclerView
    //private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: PostListAdapter
    private lateinit var dotAdapter: DotAdapter
    private var isLoading: Boolean? = false
    private var currentCity: String? = null
    private var noPosts: Boolean? = false
    private val REQUEST_CODE_CHOOSE_IMAGE = 1012
    private val REQUEST_CODE_CHOOSE_VIDEO = 1013
    private val posts: ArrayList<Post> = ArrayList()
    private var postsOver: Boolean = false
    private var isPostsLoading: Boolean = false

    private lateinit var adsPager: ViewPagerX
    private lateinit var adsPagerAdapter: AdsPagerAdapter
    private val adList = ArrayList<Ad>()
    private var foreground: Boolean = false

    private val dots = ArrayList<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.change_city_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.select_city_id) {
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
        presenter.attachView(this)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentHomeBinding) {
        // binding.toolbar.title = getString(R.string.app_name)
        adsPager = binding.adsPager
        adsPagerAdapter = AdsPagerAdapter(requireContext(), adList, adClickListener)
        adsPager.adapter = adsPagerAdapter
        adsPager.addOnPageChangeListener(onPageChangeListener)

        var dotList = binding.dotList
        dotAdapter = DotAdapter(dots) { pos ->
            selectDot(pos)
            adsPager.setCurrentItem(pos, true)
        }
        dotList.adapter = dotAdapter
        binding.addPostListener = addPostClickListener
        feedListView = binding.feedListView
        // swipeRefresh = binding.swipeRefresh
        message = binding.message
        val appPrefs by lazy { AppPrefs.getInstance(requireContext()) }
        binding.isBusiness = appPrefs.user?.userType == "BUSINESS"
        binding.currentCity = appPrefs.getValue("CITY") ?: "Select City"
        //setup list
        val layoutManager = LinearLayoutManager(context)
        feedListView.layoutManager = layoutManager
        feedListView.itemAnimator = DefaultItemAnimator()
        postAdapter = PostListAdapter(posts, requireActivity(), listener)
        feedListView.adapter = postAdapter
        feedListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (postsOver || posts.isEmpty()) return

                val lastPostPosition = layoutManager.findLastVisibleItemPosition()
                if (posts.size == lastPostPosition + 1) {
                    val post = posts[posts.size - 1]
                    fetchPostList(post.createdAt)
                }
            }
        })
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        /*  swipeRefresh.setOnRefreshListener {
              posts.clear()
              fetchPostList()
          }
  */
        binding.selectCityListener = View.OnClickListener {
            val dir = HomeFragmentDirections.actionNavigationHomeToSelectCityFragment()
            dir.onboarding = false
            findNavController().navigate(dir)
        }
    }

    private val onPageChangeListener = OnPageChangeListener { pos ->
        selectDot(pos)
    }

    private fun selectDot(pos: Int) {
        dots.forEachIndexed { index, b ->
            dots[index] = false
        }
        dots[pos] = true
        dotAdapter.notifyDataSetChanged()
    }

    private fun listenToAds() {
        AppDatabase(requireContext()).adsDao().getAll().observe(this, Observer { list ->
            adList.clear()
            adList.addAll(list)
            if (adList.isNotEmpty()) {
                adsPager.isVisible = adList.isEmpty()
                adsPagerAdapter.notifyDataSetChanged()
                adsPager.isVisible = true
                dotList.isVisible = true
                setUpDots()
                enableAutoChange()
            } else {
                adsPager.isVisible = false
                dotList.isVisible = false
            }
        })
        presenter.fetchAds()
    }

    private fun setUpDots() {
        dots.clear()
        adList.forEachIndexed { index, ad ->
            dots.add(index == 0)
        }
        dotAdapter.notifyDataSetChanged()
    }

    private fun enableAutoChange() {
        var pos = adsPager.currentItem
        Handler().postDelayed({
            adList.forEachIndexed { index, ad ->
                dots[index] = false
            }
            if (pos < adsPagerAdapter.count - 1) {
                pos++
                adsPager.setCurrentItem(pos, true)
                dots[pos] = true
            } else {
                adsPager.setCurrentItem(0, false)
                dots[0] = true
            }
            dotAdapter.notifyDataSetChanged()
            if (foreground)
                enableAutoChange()
        }, Constants.AD_BANNER_DURATION)
    }

    override fun onResume() {
        super.onResume()
        fetchPostList()
        foreground = true
    }

    override fun onPause() {
        super.onPause()
        foreground = false
    }

    private val addPostClickListener = View.OnClickListener {
        with(PermissionManager.getInstance()) {
            if (cameraPermissionGranted(requireContext())
                && writeStoragePermissionGranted(requireContext())
            ) {
                showPostDialog()
            } else {
                requestMediaPermission(this@HomeFragment)
            }
        }
    }

    private val adClickListener = object : AdsPagerAdapter.Listener {
        override fun onClick(pos: Int) {
            HomeFragmentDirections.actionNavigationHomeToAdDetailFragment(adList[pos]).let {
                findNavController().navigate(it)
            }
        }
    }

    private fun showPostDialog() {
        UploadPostDialog(object : UploadPostDialog.Listener {
            override fun videoClick() {
                pickVideo()
            }

            override fun imageClick() {
                pickImage()
            }

            override fun textClick() {
                requireActivity().launchActivity<PostTextScreen>()
            }

        }).show(fragmentManager!!, "chooser")
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
                putExtra(VideoPlayerScreen.URI_KEY, Environment.S3_BASE_URL + "${item.media}")
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


    private fun fetchPostList(timestamp: String = "") {
        if (isPostsLoading) return


    }

    override fun onAdsFetched(response: GetAdsResponse?) {
        response?.ads?.let { ads ->
            doAsync {
                AppDatabase(requireContext())
                    .adsDao().apply {
                        deleteAll()
                        save(ads)
                    }
            }
        }
    }

    override fun showProgress() {
        super.showProgress()
        isPostsLoading = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        isPostsLoading = false
    }

    override fun onPostsFetched(response: GetPostsResponse?, timestamp: String?) {
        response?.posts?.let {
            if (timestamp.isNullOrEmpty())
                posts.clear()
            postsOver = it.size < Constants.PAGE_SIZE
            posts.addAll(it)
            postAdapter.notifyDataSetChanged()
        }
    }

    override fun showPostsError(error: ApiResponse.ErrorModel) {
        requireContext().toast(error.message)
    }

    override fun showAdsError(error: ApiResponse.ErrorModel) {
//        requireContext().toast(error.message)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $requestCode, resultCode: $resultCode")
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
                /* requireActivity().launchActivity<ImagePreviewActivity> {
                     putExtra("URI", File(paths[0]).toUri())
                 }*/
                //openImageCropper(File(paths[0]).toUri())
                CropImage.activity(File(paths[0]).toUri())
                    .start(context!!, this)

            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Crop result")
            data?.let {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.getUri();
                    requireActivity().launchActivity<ImagePreviewActivity> {
                        putExtra("URI", resultUri)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.getError();
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
        Log.d(TAG, "onRequestPermissionsResult")
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }
}
