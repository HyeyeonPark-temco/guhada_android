package io.temco.guhada.view.fragment.cart

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnAddCartResultListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
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
        mViewModel = AddCartResultViewModel(this)
        initViewModel()

        mViewModel.getDeals()
        mBinding.viewModel = mViewModel

        mBinding.executePendingBindings()
        return mBinding.root
    }

    // 초기 상태 STATE_EXPANDED 변경
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var myDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        myDialog.setOnShowListener {
            val bottomSheet = myDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            var behavior  = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return myDialog
    }

    private fun initViewModel() {
        mViewModel.mDealList.observe(this, Observer {
            mBinding.recyclerviewAddcartresultRecommend.adapter = AddCartResultProductAdapter().apply {
                this.mList = it
                if(CustomLog.flag)CustomLog.L("initViewModel","mList",mList)
                this.mClickItemTask = {dealId ->
                    CommonUtil.startProductActivity(this@AddCartResultFragment.context as Activity, dealId)
                    this@AddCartResultFragment.dismiss()
                }
            }
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