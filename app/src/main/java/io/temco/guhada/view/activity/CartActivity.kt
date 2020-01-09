package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kakao.ad.common.json.ViewCart
import com.kakao.ad.tracker.send
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.common.util.TrackingUtil
import io.temco.guhada.data.model.cart.CartResponse
import io.temco.guhada.data.viewmodel.cart.CartViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.cart.CartProductAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionSpinnerAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.cart.EmptyCartFragment

/**
 * 장바구니 Activity
 * @author Hyeyeon Park
 */
class CartActivity : BindActivity<io.temco.guhada.databinding.ActivityCartBinding>() {
    private lateinit var mViewModel: CartViewModel
    private lateinit var mCartProductAdapter: CartProductAdapter
    private lateinit var mEmptyCartFragment: EmptyCartFragment
    private lateinit var mMenuSpinnerAdapter: ProductDetailOptionSpinnerAdapter

    override fun getBaseTag(): String = CartActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_cart
    override fun getViewType(): Type.View = Type.View.CART
    override fun init() {
        mBinding.includeCartHeader.setOnClickBackButton { finish() }
        mBinding.includeCartHeader.title = resources.getString(R.string.cart_title)

        initViewModel()
        initCartAdapter()

        TrackingUtil.sendViewCart()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = CartViewModel().apply {
            this.mCloseActivityTask = { message ->
                if (message.isNotEmpty()) ToastUtil.showMessage(message)
                finish()
            }
        }
        mViewModel.showDeleteDialog = {
            CustomMessageDialog(message = BaseApplication.getInstance().getString(R.string.cart_message_delete_select),
                    cancelButtonVisible = true,
                    confirmTask = {
                        mViewModel.deleteCartItem()
                    }).show(manager = (mBinding.root.context as AppCompatActivity).supportFragmentManager, tag = CartProductAdapter::class.java.simpleName)
        }
        mViewModel.clickPaymentListener = { productList, cartIdList ->
            val intent = Intent(this@CartActivity, PaymentActivity::class.java)
            intent.putExtra("productList", productList)
            intent.putExtra("cartIdList", cartIdList)
            startActivityForResult(intent, RequestCode.PAYMENT.flag)
        }
        mViewModel.cartResponse.observe(this, Observer {
            mViewModel.totalItemCount = ObservableInt(0)

            if (it.cartItemResponseList.isEmpty()) {
                showEmptyView()
            } else {
                // 기존 금액 초기화
                if (mBinding.recyclerviewCartProduct.adapter != null) {
                    mViewModel.totalProductPrice = ObservableInt(0)
                    mViewModel.totalDiscountPrice = ObservableInt(0)
                    mViewModel.totalShipPrice = ObservableInt(0)
                    mViewModel.totalPaymentPrice = ObservableInt(0)
                }

                mBinding.recyclerviewCartProduct.adapter = CartProductAdapter(mViewModel).apply { this.items = it.cartItemResponseList }
                mViewModel.onCheckedAll(true)
            }
        })


        mViewModel.cartOptionListForSpinner.observe(this, Observer {
            mCartProductAdapter.setCartItemOptionList(it)
        })

        mViewModel.getCart(invalidTokenTask = {
            LoadingIndicatorUtil(BaseApplication.getInstance()).hide()
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            redirectLoginActivity()
        })

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initCartAdapter() {
        mCartProductAdapter = CartProductAdapter(mViewModel)
        mBinding.recyclerviewCartProduct.adapter = mCartProductAdapter
        (mBinding.recyclerviewCartProduct.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    private fun setTotalPrice(cartResponse: CartResponse) {
        mViewModel.totalProductPrice = ObservableInt(cartResponse.totalPaymentPrice)
        mViewModel.totalDiscountPrice = ObservableInt(cartResponse.totalDiscountDiffPrice)
        mViewModel.totalPaymentPrice = ObservableInt(cartResponse.totalPaymentPrice)
        mViewModel.notifyPropertyChanged(BR.totalProductPrice)
        mViewModel.notifyPropertyChanged(BR.totalDiscountPrice)
        mViewModel.notifyPropertyChanged(BR.totalPaymentPrice)
    }

    private fun showEmptyView() {
        if (!::mEmptyCartFragment.isInitialized) mEmptyCartFragment = EmptyCartFragment()
        supportFragmentManager.beginTransaction().let { fragmentTransaction ->
            mBinding.constraintlayoutCartContent.visibility = View.GONE
            fragmentTransaction.add(mBinding.framelayoutCartEmpty.id, mEmptyCartFragment)
            fragmentTransaction.commitAllowingStateLoss()
            mBinding.framelayoutCartEmpty.bringToFront()
        }
    }

    private fun redirectLoginActivity() {
        Intent(this@CartActivity, LoginActivity::class.java).let {
            this@CartActivity.startActivityForResult(it, Flag.RequestCode.LOGIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Flag.RequestCode.LOGIN ->
                if (resultCode == Activity.RESULT_OK) mViewModel.getCart()
                else finish()
            RequestCode.PAYMENT.flag -> {
                if (resultCode == Activity.RESULT_OK) finish()
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["isZero", "marginTop"])
        fun setMarginTop(view: View, isZero: Boolean, marginTop: Float) {
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            if (isZero) layoutParams.setMargins(0, 0, 0, 0)
            else layoutParams.setMargins(0, marginTop.toInt(), 0, 0)
            view.layoutParams = layoutParams
        }
    }
}