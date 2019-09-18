package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.cart.CartResponse
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.viewmodel.cart.CartViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.cart.CartOptionAdapter
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

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = CartViewModel()
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
            startActivity(intent)
        }
        mViewModel.cartResponse.observe(this, Observer {
            mViewModel.allChecked = ObservableBoolean(false)
            if (it.cartItemResponseList.isEmpty()) {
                showEmptyView()
            } else {
                if (mBinding.recyclerviewCartProduct.adapter != null)
                    (mBinding.recyclerviewCartProduct.adapter as CartProductAdapter).setItems(it.cartItemResponseList)
            }
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        })

//         GRID OPTION LIST
//        mViewModel.cartOptionList.observe(this, Observer {
//            val holder = mBinding.recyclerviewCartProduct.findViewHolderForAdapterPosition(mViewModel.shownMenuPos) as CartProductAdapter.Holder
//            mCartProductAdapter.setCartItemOptionList(it)
//            (holder.binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(it)
//        })

        mViewModel.cartOptionListForSpinner.observe(this, Observer {
           // val holder = mBinding.recyclerviewCartProduct.findViewHolderForAdapterPosition(mViewModel.shownMenuPos) as CartProductAdapter.Holder
            mCartProductAdapter.setCartItemOptionList(it)
          //  (holder.binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(it)
        })


        mViewModel.getCart(invalidTokenTask = {
            LoadingIndicatorUtil(BaseApplication.getInstance()).hide()
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            redirectLoginActivity()
        })
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