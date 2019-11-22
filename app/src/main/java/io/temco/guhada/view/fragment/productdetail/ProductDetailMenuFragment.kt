package io.temco.guhada.view.fragment.productdetail

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ListPopupWindow
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.view.CustomSpinner
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
    var mViewModel: ProductDetailMenuViewModel = ProductDetailMenuViewModel(this)
    private lateinit var mMenuSpinnerAdapter: ProductDetailOptionSpinnerAdapter
    var mIsBottomPopup = false
    private var mSpinnerFirstInit = false

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
        setSpinnerHeight()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    /**
     * 옵션 드롭다운 스피너로 변경
     * @since 2019.09.05
     * @author Hyeyeon Park
     */
    private fun initMenuSpinner() {
        val list = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
        if (list.isNotEmpty())
            mBinding.textviewProductdetailOption.text = list[0].getOptionNameText()

        mMenuSpinnerAdapter = ProductDetailOptionSpinnerAdapter(
                context = mBinding.root.context,
                layout = R.layout.item_productdetail_optionspinner,
                list = list.toList())

        mBinding.spinnerProductdetailOption.mListener = object : CustomSpinner.OnCustomSpinnerListener {
            override fun onSpinnerOpened() {
                mViewModel.mIsSpinnerOpen = ObservableBoolean(!mViewModel.mIsSpinnerOpen.get())
                mViewModel.notifyPropertyChanged(BR.mIsSpinnerOpen)
            }

            override fun onSpinnerClosed() {
                mViewModel.mIsSpinnerOpen = ObservableBoolean(!mViewModel.mIsSpinnerOpen.get())
                mViewModel.notifyPropertyChanged(BR.mIsSpinnerOpen)
            }
        }
        mBinding.spinnerProductdetailOption.adapter = mMenuSpinnerAdapter
        mBinding.spinnerProductdetailOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val optionList = mViewModel.product.optionInfos ?: listOf()
                if (mSpinnerFirstInit && position < optionList.size)
                    setOption(optionList[position])
                mSpinnerFirstInit = true
            }
        }

        mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.GONE

        // 스피너 드롭다운 Max Height 5개 높이로 설정
        val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val popupWindow = popup.get(mBinding.spinnerProductdetailOption) as androidx.appcompat.widget.ListPopupWindow
        if (list.size > 4) popupWindow.height = CommonViewUtil.convertDpToPixel(230, mBinding.root.context)
    }

    private fun initMenuList() {
        val list = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
        if (list.isNotEmpty()) {
            val placeHolder = list[0].getOptionNameText()
            mBinding.textviewProductdetailOption.text = placeHolder
        }

        mBinding.framelayoutProductdetailOptionbutton.setOnClickListener {
            collapseOptionList(isExpanded = mViewModel.mIsBottomSpinnerOpen.get())
        }

        mBinding.recyclerviewProductdetailOptionspinner.adapter = ProductDetailOptionListAdapter().apply {
            this.mList = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
            this.mItemClickTask = { option ->
                collapseOptionList(isExpanded = true)
                setOption(option)
            }
        }
    }

    private fun collapseOptionList(isExpanded: Boolean) {
        if (isExpanded) {
            mViewModel.mIsBottomSpinnerOpen = ObservableBoolean(false)
            mBinding.framelayoutProductdetailOptionbutton.setBackgroundResource(R.drawable.border_all_whitefour)
        } else {
            mBinding.framelayoutProductdetailOptionbutton.setBackgroundResource(R.drawable.border_all_whitefour_emptybottom)
            mViewModel.mIsBottomSpinnerOpen = ObservableBoolean(true)
        }

        mViewModel.notifyPropertyChanged(BR.mIsBottomSpinnerOpen)
    }

    private fun setOption(option: OptionInfo?) {
        if (option != null && option.stock > 0) {
            if (option.rgb1?.isNotEmpty() == true) {
                mBinding.imageviewProductdetailOptionselected.visibility = View.VISIBLE
                mBinding.imageviewProductdetailOptionselected.setBackgroundColor(Color.parseColor(option.rgb1))
            } else mBinding.imageviewProductdetailOptionselected.visibility = View.GONE

            mBinding.linearlayoutProductdetailOption.visibility = View.GONE
            mBinding.textviewProductdetailOptionselected.text = option.getOptionText().plus(" ${String.format(mBinding.root.context.getString(R.string.productdetail_option_extraprice_format), option.price)}")
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

    // TODO list visible limit: 5; height: 230dp [2019.09.05]
    private fun setSpinnerHeight() {
        val spinner = mBinding.spinnerProductdetailOption
        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val window = popup.get(spinner) as ListPopupWindow
        window.height = CommonViewUtil.convertDpToPixel(dp = 230, context = context
                ?: mBinding.root.context)
    }

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