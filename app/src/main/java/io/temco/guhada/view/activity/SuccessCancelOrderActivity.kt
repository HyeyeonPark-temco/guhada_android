package io.temco.guhada.view.activity

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.databinding.ActivitySuccesscancelorderBinding
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 주문 취소 완료 Activity
 * @author Hyeyeon Park
 * @since 2019.08.07
 */
class SuccessCancelOrderActivity : BindActivity<ActivitySuccesscancelorderBinding>() {
    override fun getBaseTag(): String = SuccessCancelOrderActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_successcancelorder
    override fun getViewType(): Type.View = Type.View.SUCCESS_CANCEL_ORDER

    override fun init() {
        initTitle()
        initProductInfo()
    }

    private fun initTitle() {
        val title1 = getString(R.string.successcancelorder_title1)
        val title2 = getString(R.string.successcancelorder_title2)
        val title3 = getString(R.string.successcancelorder_title3)
        val title4 = getString(R.string.successcancelorder_title4)
        val spannable = SpannableString("$title1 $title2\n$title3$title4")
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.common_blue_purple)), title1.length, title1.length + title2.length + title3.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding.textviewSuccesscancelorderTitle.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun initProductInfo() {
        intent.getSerializableExtra("purchaseOrder").let {
            if (it != null) {
                val purchaseOrder = it as PurchaseOrder
                mBinding.includeSuccesscancelorderProductinfo.imageUrl = purchaseOrder.imageUrl
                mBinding.includeSuccesscancelorderProductinfo.brandName = purchaseOrder.brandName
                mBinding.includeSuccesscancelorderProductinfo.productName = "${purchaseOrder.season} ${purchaseOrder.dealName}"
                mBinding.includeSuccesscancelorderProductinfo.optionStr = purchaseOrder.getOptionStr()
                mBinding.includeSuccesscancelorderProductinfo.price = purchaseOrder.discountPrice
                mBinding.includeSuccesscancelorderProductinfo.purchaseStatusText = purchaseOrder.purchaseStatusText
                mBinding.executePendingBindings()
            }
        }

        mBinding.setOnClickClose { finish() }
        mBinding.executePendingBindings()
    }
}