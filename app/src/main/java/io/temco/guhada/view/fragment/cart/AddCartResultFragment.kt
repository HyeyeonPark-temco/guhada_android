package io.temco.guhada.view.fragment.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnAddCartResultListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.data.viewmodel.AddCartResultViewModel
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.FragmentAddcartresultBinding
import io.temco.guhada.view.activity.CartActivity

/**
 * 장바구니 담기 완료 Fragment
 * @author Hyeyeon Park
 */
class AddCartResultFragment(val mListener: OnProductDetailListener) : BottomSheetDialogFragment(), OnAddCartResultListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentAddcartresultBinding>(inflater, R.layout.fragment_addcartresult, container, false)
        binding.viewModel = AddCartResultViewModel(this)
        binding.executePendingBindings()
        return binding.root
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