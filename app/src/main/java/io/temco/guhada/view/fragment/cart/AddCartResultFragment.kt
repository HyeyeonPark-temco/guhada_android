package io.temco.guhada.view.fragment.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnAddCartResultListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.data.viewmodel.cart.AddCartResultViewModel
import io.temco.guhada.databinding.FragmentAddcartresultBinding
import io.temco.guhada.view.activity.CartActivity
import io.temco.guhada.view.adapter.cart.AddCartResultProductAdapter

/**
 * 장바구니 담기 완료 Fragment
 * @author Hyeyeon Park
 */
class AddCartResultFragment(val mListener: OnProductDetailListener) : BottomSheetDialogFragment(), OnAddCartResultListener {
    private lateinit var mBinding: FragmentAddcartresultBinding
    private lateinit var mViewModel: AddCartResultViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_addcartresult, container, false)
        initViewModel()
        mViewModel.getDeals()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
        return mBinding.root
    }

    private fun initViewModel() {
        mViewModel = AddCartResultViewModel(this)
        mViewModel.mDealList.observe(this, Observer {
            mBinding.recyclerviewAddcartresultRecommend.adapter = AddCartResultProductAdapter().apply {
                this.mList = it
            }
//            (mBinding.recyclerviewAddcartresultRecommend.adapter as).notifyDataSetChanged()
        })
    }

    override fun onDestroyOptionsMenu() {
        dismiss()
        super.onDestroyOptionsMenu()
    }

    override fun hide() = dismiss()

    override fun redirectProductDetail() {
        mListener.dismissOptionMenu()
        dismiss()
        startActivity(Intent(context, CartActivity::class.java))
    }

}