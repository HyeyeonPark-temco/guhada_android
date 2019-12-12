package io.temco.guhada.view.fragment.productdetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionAttrAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionListAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionSpinnerAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 상품 상세-옵션 선택 view
 * @author Hyeyeon Park
 */
class ProductDetailMenuFragment : BaseFragment<io.temco.guhada.databinding.LayoutProductdetailMenuBinding>(), OnProductDetailMenuListener {
    private lateinit var mMenuSpinnerAdapter: ProductDetailOptionSpinnerAdapter
    var mViewModel: ProductDetailMenuViewModel = ProductDetailMenuViewModel(this)
    var mIsBottomPopup = false

    override fun getBaseTag(): String = ProductDetailMenuFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.layout_productdetail_menu

    // OnProductDetailMenuListener
    override fun showMessage(message: String) {
        ToastUtil.showMessage(message)
    }

    override fun closeMenu() {}
    override fun setColorName(optionAttr: OptionAttr, task: () -> Unit) {}

    override fun init() {
        if (mIsBottomPopup) initMenuList()
        else initMenuSpinner()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    /**
     * 상단 옵션 선택 view
     *
     * 옵션 드롭다운 스피너로 변경
     * @since 2019.09.05
     *
     * CustomSpinnerView로 변경
     * @since 2019.12.12
     *
     * @author Hyeyeon Park
     */
    private fun initMenuSpinner() {
        val list = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
        if (list.isNotEmpty())
            mBinding.spinnerProductdetailOptionlist.setPlaceHolder(list[0].getOptionNameText())

        mMenuSpinnerAdapter = ProductDetailOptionSpinnerAdapter(
                context = mBinding.root.context,
                layout = R.layout.item_productdetail_optionspinner,
                list = list.toList())

        mBinding.linearlayoutProductdetailOptionselected.visibility =  View.GONE
        mBinding.spinnerProductdetailOptionlist.visibility = View.VISIBLE
        mBinding.spinnerProductdetailOptionlist.setAdapter(mMenuSpinnerAdapter)
        mBinding.spinnerProductdetailOptionlist.setOnItemClickTask { position ->
            val item = list[position]
            mBinding.spinnerProductdetailOptionlist.setPlaceHolder("${item.getOptionText()} ${item.getExtraPriceText()}")
            mBinding.spinnerProductdetailOptionlist.setRgb(item.rgb1)
            setOption(item)
        }
    }

    /**
     * 하단 옵션 선택 view
     */
    private fun initMenuList() {
        val list = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
        if (list.isNotEmpty()) {
            mBinding.spinnerProductdetailOptionlist.setPlaceHolder(list[0].getOptionNameText())
            val placeHolder = list[0].getOptionNameText()
             mBinding.textviewProductdetailOptionselected.text = placeHolder
        }

        mBinding.linearlayoutProductdetailOptionselected.setOnClickListener {
            collapseOptionList(isExpanded = mViewModel.mIsBottomSpinnerOpen.get())
        }

        mBinding.recyclerviewProductdetailOptionspinner.adapter = ProductDetailOptionListAdapter().apply {
            this.mList = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
            this.mItemClickTask = { option ->
                collapseOptionList(isExpanded = true)
                setOption(option)
            }
        }

        mBinding.spinnerProductdetailOptionlist.visibility = View.GONE
    }

    private fun collapseOptionList(isExpanded: Boolean) {
        mViewModel.mIsBottomSpinnerOpen = ObservableBoolean(!isExpanded)
        mViewModel.notifyPropertyChanged(BR.mIsBottomSpinnerOpen)
    }

    @SuppressLint("SetTextI18n")
    private fun setOption(option: OptionInfo?) {
        if (option != null && option.stock > 0) {
            if (option.rgb1?.isNotEmpty() == true) {
                mBinding.imageviewProductdetailOptionselected.visibility = View.VISIBLE
                mBinding.imageviewProductdetailOptionselected.setBackgroundColor(Color.parseColor(option.rgb1))
            } else mBinding.imageviewProductdetailOptionselected.visibility = View.GONE

            mBinding.textviewProductdetailOptionselected.text =  "${option.getOptionText()} ${option.getExtraPriceText()}"
            mBinding.executePendingBindings()

            // INIT OPTION
            mViewModel.mSelectedOptionInfo = option
            mViewModel.productCount = ObservableInt(1)
            mViewModel.notifyPropertyChanged(BR.productCount)

            // PRICE
            mViewModel.extraPrice = ObservableInt(option.price)
            mViewModel.totalPrice = ObservableInt(mViewModel.product.discountPrice + option.price)
            mViewModel.notifyPropertyChanged(BR.extraPrice)
            mViewModel.notifyPropertyChanged(BR.totalPrice)
        }
    }

    // 미사용
    private fun initMenuGrid() {
        mBinding.recyclerviewProductdetailMenu.adapter = ProductDetailOptionAdapter(mViewModel).apply {
            mViewModel.product.options
            this.mOptionInfoList = mViewModel.product.optionInfos?.toMutableList()
                    ?: mutableListOf()
        }
    }

    fun getProductCount(): Int = mViewModel.productCount.get()
    fun getTotalPrice(): Int = mViewModel.totalPrice.get()
    fun getSelectedOptionCount(): Int = mViewModel.optionMap.keys.size
    fun getSelectedOptionAttrs() = mViewModel.optionMap
    fun getSelectedOptionDealId(): Long? = mViewModel.getDealOptionId()

    companion object {
        // 미사용
        @JvmStatic
        @BindingAdapter("productOption")
        fun RecyclerView.bindOption(list: List<Option>?) {
            if (list != null && list.isNotEmpty() && this.adapter != null) {
                (this.adapter as ProductDetailOptionAdapter).setItems(list)
            }
        }

        // 미사용
        @JvmStatic
        @BindingAdapter("productOptionAttr")
        fun RecyclerView.bindOptionAttr(option: Option?) {
            if (option != null && this.adapter != null) {
                val attrList: MutableList<OptionAttr> = ArrayList()

                if (option.rgb.isNotEmpty()) {
                    for (i in 0 until option.rgb.size) {
                        OptionAttr().apply {
                            rgb = option.rgb[i]
                            name = option.attributes[i]

                            for (item in option.attributeItems) {
                                enabled = if (item.name == name) item.enabled
                                else false
                            }

                        }.let {
                            attrList.add(it)
                        }
                    }
                } else {
                    for (attr in option.attributes) {
                        OptionAttr().apply {
                            name = attr

                            for (item in option.attributeItems) {
                                enabled = if (item.name == name) item.enabled
                                else false
                            }

                        }.let {
                            attrList.add(it)
                        }
                    }
                }

                (this.adapter as ProductDetailOptionAttrAdapter).setItems(attrList.toList())
            }
        }

    }
}