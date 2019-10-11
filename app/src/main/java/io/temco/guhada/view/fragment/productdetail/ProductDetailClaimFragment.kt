package io.temco.guhada.view.fragment.productdetail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag.RequestCode.LOGIN
import io.temco.guhada.common.Flag.RequestCode.WRITE_CLAIM
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailClaimViewModel
import io.temco.guhada.databinding.LayoutProductdetailClaimBinding
import io.temco.guhada.view.activity.LoginActivity
import io.temco.guhada.view.activity.WriteClaimActivity
import io.temco.guhada.view.adapter.productdetail.ProductDetailClaimAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 상품상세-상품 문의
 * @author Hyeyeon Park
 */
class ProductDetailClaimFragment : BaseFragment<LayoutProductdetailClaimBinding>() {
    var productId: Long = 0L
    // 판매자 문의하기를 위해 셀러 id추가
    var sellerId: Long = 0L
    private val ALL_CLAIMS = 0
    private val PENDING_CLAIMS = 1
    private val COMPLETED_CLAIMS = 2
    private lateinit var mViewModel: ProductDetailClaimViewModel
    lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag(): String = ProductDetailClaimFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_claim

    override fun init() {
        if (productId > 0) {
            mLoadingIndicatorUtil = LoadingIndicatorUtil(mBinding.root.context)
            mViewModel = ProductDetailClaimViewModel(productId, object : OnProductDetailClaimListener {
                override fun showMessage(message: String) {
                    ToastUtil.showMessage(message)
                }

                override fun redirectWriteClaimActivity() {
                    val intent = Intent(this@ProductDetailClaimFragment.context, WriteClaimActivity::class.java)
                    intent.putExtra("productId", productId)
                    activity?.startActivityForResult(intent, WRITE_CLAIM)
                }

                /**
                 * @author park jungho
                 * 판매자 문의하기
                 */
                override fun redirectUserClaimSellerActivity() {
                    CommonUtilKotlin.startActivityUserClaimSeller(context as AppCompatActivity, sellerId, productId, -1)
                }

                override fun clearClaims() {
                    (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).clearItems()
                }

                override fun redirectLoginActivity() {
                    val intent = Intent(this@ProductDetailClaimFragment.context, LoginActivity::class.java)
                    activity?.startActivityForResult(intent, LOGIN)
                }
            })

            mViewModel.claimResponse.observe(this, Observer {
                if (mBinding.recyclerviewProductdetailClaim.adapter == null) mBinding.recyclerviewProductdetailClaim.adapter = ProductDetailClaimAdapter()

                if (mViewModel.claimPageNo > 1) (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).addItems(it.content)
                else (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).setItems(it.content)

                mBinding.framelayoutProductdetailClaimmore.visibility = if (it.last) View.GONE else View.VISIBLE
                mBinding.textviewProductdetailQnaCount.text = it.totalElements.toString()
            })

            mBinding.viewModel = mViewModel
            mBinding.recyclerviewProductdetailClaim.adapter = ProductDetailClaimAdapter()
            mBinding.tablayoutProductdetailClaim.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        ALL_CLAIMS -> mViewModel.claimStatus = ""
                        PENDING_CLAIMS -> mViewModel.claimStatus = "PENDING"
                        COMPLETED_CLAIMS -> mViewModel.claimStatus = "COMPLETED"
                    }
                    refreshClaims()
                }
            })
            mViewModel.mineVisibility = if (Preferences.getToken() != null) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)

            mViewModel.getClaims()
            mBinding.executePendingBindings()
        }
    }


    fun refreshIsMineVisible() {
        if (::mViewModel.isInitialized) {
            mViewModel.mineVisibility = if (Preferences.getToken() != null) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)
            mViewModel.notifyPropertyChanged(BR.mineVisibility)
        }
    }

    fun refreshClaims() {
        (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).clearItems()
        mViewModel.claimPageNo = 0
        mViewModel.claimPageSize = 5
        mViewModel.claimResponse.postValue(ClaimResponse())
        mViewModel.getClaims()
    }

    inner class WrapContentLinearLayoutManager(context: Context?, @RecyclerView.Orientation orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context) {
        init {
            setOrientation(orientation)
            setReverseLayout(reverseLayout)
        }

        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("linearLayoutManager", "meet IOOBE in RecyclerView")
            }
        }
    }

    interface OnProductDetailClaimListener {
        fun showMessage(message: String)
        fun redirectWriteClaimActivity()
        fun redirectUserClaimSellerActivity()
        fun clearClaims()
        fun redirectLoginActivity()
    }
}