package io.temco.guhada.view.fragment.productdetail

import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.viewmodel.ProductDetailClaimViewModel
import io.temco.guhada.databinding.LayoutProductdetailClaimBinding
import io.temco.guhada.view.adapter.ClaimAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailClaimFragment(private val productId: Int) : BaseFragment<LayoutProductdetailClaimBinding>() {
    private val ALL_CLAIMS = 0
    private val PENDING_CLAIMS = 1
    private val COMPLETED_CLAIMS = 2
    private lateinit var mViewModel: ProductDetailClaimViewModel

    companion object {
        @JvmStatic
        @BindingAdapter("productClaims")
        fun RecyclerView.bindClaims(list: MutableList<ClaimResponse.Claim>?) {
            if (list != null) {
                if (this.adapter == null) {
                    this.adapter = ClaimAdapter()
                }

                if ((this.adapter as ClaimAdapter).itemCount > 0) {
                    // MORE
                    (this.adapter as ClaimAdapter).addItems(list)
                } else {
                    (this.adapter as ClaimAdapter).setItems(list)
                }

            }
        }
    }


    override fun getBaseTag(): String = ProductDetailClaimFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_claim

    override fun init() {
        mViewModel = ProductDetailClaimViewModel(productId, object : OnProductDetailClaimListener {
            override fun showMessage(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })
        mViewModel.getClaims()

        mBinding.viewModel = mViewModel
        mBinding.recyclerviewProductdetailClaim.adapter = ClaimAdapter()
        mBinding.recyclerviewProductdetailClaim.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mBinding.tablayoutProductdetailClaim.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mBinding.recyclerviewProductdetailClaim.adapter = ClaimAdapter()
                when (tab?.position) {
                    ALL_CLAIMS -> mViewModel.claimStatus = ""
                    PENDING_CLAIMS -> mViewModel.claimStatus = "PENDING"
                    COMPLETED_CLAIMS -> mViewModel.claimStatus = "COMPLETED"
                }

                mViewModel.claimPageNo = 0
                mViewModel.getClaims()
            }
        })
        mBinding.executePendingBindings()
    }

    interface OnProductDetailClaimListener {
        fun showMessage(message: String)
    }
}