package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivitySellerstoreBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.fragment.ListBottomSheetFragment

class SellerInfoActivity : BindActivity<ActivitySellerstoreBinding>() {
    override fun getBaseTag(): String = SellerInfoActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_sellerstore

    override fun getViewType(): Type.View = Type.View.SELLER_INFO

    override fun init() {
        mBinding.imagebuttonSellerstoreMore.setOnClickListener {
            ListBottomSheetFragment(this).apply { mList = mutableListOf("1번","2번","3번") }.show(supportFragmentManager, baseTag)
        }
    }
}