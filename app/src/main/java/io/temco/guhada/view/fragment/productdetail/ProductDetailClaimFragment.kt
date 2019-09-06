package io.temco.guhada.view.fragment.productdetail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag.RequestCode.LOGIN
import io.temco.guhada.common.Flag.RequestCode.WRITE_CLAIM
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailClaimViewModel
import io.temco.guhada.databinding.LayoutProductdetailClaimBinding
import io.temco.guhada.view.activity.LoginActivity
import io.temco.guhada.view.activity.WriteClaimActivity
import io.temco.guhada.view.adapter.productdetail.ProductDetailClaimAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailClaimFragment : BaseFragment<LayoutProductdetailClaimBinding>() {
    var productId: Long = 0L
    // 판매자 문의하기를 위해 셀러 id추가
    var sellerId: Long = 0L
    private val ALL_CLAIMS = 0
    private val PENDING_CLAIMS = 1
    private val COMPLETED_CLAIMS = 2
    private lateinit var mViewModel: ProductDetailClaimViewModel

    override fun getBaseTag(): String = ProductDetailClaimFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_claim

    override fun init() {
        mViewModel = ProductDetailClaimViewModel(productId, object : OnProductDetailClaimListener {
            override fun showMessage(message: String) {
                ToastUtil.showMessage(message)
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                CommonUtilKotlin.startActivityUserClaimSeller(context as AppCompatActivity, sellerId, productId,-1)
            }

            override fun clearClaims() {
                (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).clearItems()
            }

            override fun redirectLoginActivity() {
                val intent = Intent(this@ProductDetailClaimFragment.context, LoginActivity::class.java)
                activity?.startActivityForResult(intent, LOGIN)
            }
        })
        mBinding.viewModel = mViewModel
        mBinding.recyclerviewProductdetailClaim.adapter = ProductDetailClaimAdapter()
        mBinding.recyclerviewProductdetailClaim.layoutManager = WrapContentLinearLayoutManager(context, RecyclerView.VERTICAL, false)
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

    fun refreshIsMineVisible() {
        mViewModel.mineVisibility = if (Preferences.getToken() != null) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)
        mViewModel.notifyPropertyChanged(BR.mineVisibility)
    }

    fun refreshClaims() {
        (mBinding.recyclerviewProductdetailClaim.adapter as ProductDetailClaimAdapter).clearItems()
        mViewModel.claimPageNo = 0
        mViewModel.claimPageSize = 5
        mViewModel.claimResponse = ClaimResponse()
        mViewModel.getClaims()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("productClaims")
        fun RecyclerView.bindClaims(list: MutableList<Claim>?) {
            if (list != null) {
                if (this.adapter == null) {
                    this.adapter = ProductDetailClaimAdapter()
                }

                if ((this.adapter as ProductDetailClaimAdapter).itemCount > 0) {
                    // MORE
                    (this.adapter as ProductDetailClaimAdapter).addItems(list)
                } else {
                    (this.adapter as ProductDetailClaimAdapter).setItems(list)
                }
            }
        }
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