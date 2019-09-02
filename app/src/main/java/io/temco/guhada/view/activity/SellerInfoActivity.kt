package io.temco.guhada.view.activity

import android.view.View
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.ProductOrderType
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.SellerInfoViewModel
import io.temco.guhada.databinding.ActivitySellerstoreBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.SellerInfoProductAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment

/**
 * 셀러 스토어(셀러 회원 정보 화면) Activity
 * @author Hyeyeon Park
 * @since 2019.08.30
 */
class SellerInfoActivity : BindActivity<ActivitySellerstoreBinding>() {
    private lateinit var mViewModel: SellerInfoViewModel
    private lateinit var mFilterFragment: ListBottomSheetFragment

    private lateinit var mMenuFragment: ListBottomSheetFragment

    override fun getBaseTag(): String = SellerInfoActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_sellerstore

    override fun getViewType(): Type.View = Type.View.SELLER_INFO

    override fun init() {
        initViewModel()

        mBinding.imagebuttonSellerstoreMore.setOnClickListener {
            if (!::mMenuFragment.isInitialized) {
                mMenuFragment = ListBottomSheetFragment(this).apply {
                    mTitle = this@SellerInfoActivity.getString(R.string.common_more)
                    mList = mutableListOf(
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu1),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu2),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu3),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu4),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu5),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu6))
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            when (position) {
                                SellerInfoViewModel.SellerInfoMore.SELLER_STORE.pos -> {
                                }
                                SellerInfoViewModel.SellerInfoMore.SELLER_INFO.pos -> {
                                }
                                SellerInfoViewModel.SellerInfoMore.REVIEW.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.BOARD.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.COMMENT.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.REPORT.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                            }
                        }

                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
            }
            mMenuFragment.show(supportFragmentManager, baseTag)
        }
        mBinding.linearlayoutSellerstoreFilter.setOnClickListener {
            if (!::mFilterFragment.isInitialized) {
                mFilterFragment = ListBottomSheetFragment(this@SellerInfoActivity).apply {
                    mList = mutableListOf(ProductOrderType.DATE.label, ProductOrderType.SCORE.label, ProductOrderType.PRICE_ASC.label, ProductOrderType.PRICE_DESC.label)
                    mTitle = this@SellerInfoActivity.getString(R.string.product_order_title)
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            when (position) {
                                ProductOrderType.DATE.position -> {
                                    mViewModel.mOrder = ProductOrderType.DATE.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.DATE.label
                                }
                                ProductOrderType.SCORE.position -> {
                                    mViewModel.mOrder = ProductOrderType.SCORE.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.SCORE.label
                                }
                                ProductOrderType.PRICE_ASC.position -> {
                                    mViewModel.mOrder = ProductOrderType.PRICE_ASC.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.PRICE_ASC.label
                                }
                                ProductOrderType.PRICE_DESC.position -> {
                                    mViewModel.mOrder = ProductOrderType.PRICE_DESC.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.PRICE_DESC.label
                                }
                            }

                            mViewModel.mPage = 1
                            mViewModel.getSellerProductList()
                        }

                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
            }
            mFilterFragment.show(supportFragmentManager, baseTag)
        }

        mViewModel.getSellerInfo()
        mViewModel.getSellerBookMark()
        mViewModel.getSellerSatisfaction()
        mViewModel.getSellerFollowCount()
        mViewModel.getSellerProductList()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = SellerInfoViewModel().apply {
            intent.getLongExtra("sellerId", 251).let {
                if (it > 0)
                    this.mSellerId = it
                else {
                    ToastUtil.showMessage(this@SellerInfoActivity.getString(R.string.common_message_error))
                    finish()
                }
            }
        }
        mViewModel.mSeller.observe(this@SellerInfoActivity, Observer { mBinding.seller = it })
        mViewModel.mSellerBookMark.observe(this@SellerInfoActivity, Observer { mBinding.bookMark = it })
        mViewModel.mSellerSatisfaction.observe(this@SellerInfoActivity, Observer { mBinding.satisfaction = it })
        mViewModel.mSellerFollowerCount.observe(this@SellerInfoActivity, Observer {
            mBinding.textviewSellerstoreFollowercount.text = String.format(this@SellerInfoActivity.getString(R.string.common_format_people), it.bookmarkCount)
        })
        mViewModel.mSellerProductList.observe(this@SellerInfoActivity, Observer {
            if (mViewModel.mPage == 1) // first
                mBinding.recyclerivewSellerstoreProductlist.adapter = SellerInfoProductAdapter().apply { mList = it.deals }
            else { // more
                (mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).mList.addAll(it.deals)
                (mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).notifyDataSetChanged()

                if ((mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).mList.size == it.countOfDeals)
                    mBinding.linearlayoutSellerstoreMore.visibility = View.GONE
            }
        })
    }
}

