package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.viewmodel.CartViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.cart.CartOptionAdapter
import io.temco.guhada.view.adapter.cart.CartProductAdapter
import io.temco.guhada.view.fragment.cart.EmptyCartFragment

class CartActivity : BindActivity<io.temco.guhada.databinding.ActivityCartBinding>() {
    private lateinit var mViewModel: CartViewModel
    private lateinit var mCartProductAdapter: CartProductAdapter
    private lateinit var mEmptyCartFragment: EmptyCartFragment

    override fun getBaseTag(): String = CartActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_cart
    override fun getViewType(): Type.View = Type.View.CART
    override fun init() {
        mBinding.includeCartHeader.setOnClickBackButton { finish() }
        mBinding.includeCartHeader.title = resources.getString(R.string.cart_title)

        mViewModel = CartViewModel()
        mViewModel.cartResponse.observe(this, Observer {
            if (it.cartItemResponseList.isEmpty()) {
                if (!::mEmptyCartFragment.isInitialized)
                    mEmptyCartFragment = EmptyCartFragment()
                supportFragmentManager.beginTransaction().let { fragmentTransaction ->
                    fragmentTransaction.add(mBinding.framelayoutCartEmpty.id, mEmptyCartFragment)
                    fragmentTransaction.commitAllowingStateLoss()
                    mBinding.framelayoutCartEmpty.bringToFront()
                }
            } else {
                if (mBinding.recyclerviewCartProduct.adapter != null)
                    (mBinding.recyclerviewCartProduct.adapter as CartProductAdapter).setItems(it.cartItemResponseList)
            }
            mBinding.executePendingBindings()
        })
        mViewModel.cartOptionList.observe(this, Observer {
            val holder = mBinding.recyclerviewCartProduct.findViewHolderForAdapterPosition(mViewModel.shownMenuPos) as CartProductAdapter.Holder
            mCartProductAdapter.setCartItemOptionList(it)
            (holder.binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(it)
        })
        mViewModel.getCart(invalidTokenTask = {
            LoadingIndicatorUtil(BaseApplication.getInstance()).hide()
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            redirectLoginActivity()
        })
        mCartProductAdapter = CartProductAdapter(mViewModel)

        mBinding.recyclerviewCartProduct.adapter = mCartProductAdapter
        (mBinding.recyclerviewCartProduct.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mBinding.viewModel = mViewModel

        mBinding.executePendingBindings()
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

        @JvmStatic
        @BindingAdapter("cart")
        fun RecyclerView.setCartItems(list: MutableList<Cart>) {
//            if (this.adapter != null) {
//                (this.adapter as CartProductAdapter).setItems(list)
//            }
        }
    }
}