package io.temco.guhada.view.fragment.productdetail

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListPopupWindow
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.CommonViewUtil
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
    var mViewModel: ProductDetailMenuViewModel = ProductDetailMenuViewModel(this)
    private lateinit var mMenuSpinnerAdapter: ProductDetailOptionSpinnerAdapter
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
        if (list.isNotEmpty()) {
            var placeHolder = ""
            if (!list[0].label1.isNullOrEmpty())
                placeHolder += list[0].label1
            if (!list[0].label2.isNullOrEmpty())
                placeHolder += ", ${list[0].label2}"
            if (!list[0].label3.isNullOrEmpty())
                placeHolder += ", ${list[0].label3}"

            list.add(OptionInfo().apply { this.attribute1 = if (placeHolder.isEmpty()) BaseApplication.getInstance().getString(R.string.productdetail_message_selectoption) else placeHolder })
            mBinding.textviewProductdetailOption.text = placeHolder
        }

        mMenuSpinnerAdapter = ProductDetailOptionSpinnerAdapter(
                context = mBinding.root.context,
                layout = R.layout.item_productdetail_optionspinner,
                list = list.toList())
        mBinding.spinnerProductdetailOption.adapter = mMenuSpinnerAdapter
        mBinding.spinnerProductdetailOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val optionList = mViewModel.product.optionInfos ?: listOf()
                if (optionList.size > position && optionList[position].stock > 0) {
                    val option = optionList[position]
                    mBinding.linearlayoutProductdetailOption.visibility = View.GONE
                    mBinding.imageviewProductdetailOptionselected.setBackgroundColor(Color.parseColor(option.rgb1))
                    mBinding.textviewProductdetailOptionselected.text = mMenuSpinnerAdapter.getOptionText(option)
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
        }
        mBinding.spinnerProductdetailOption.setSelection(list.size - 1)

        mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.GONE
    }

    private fun initMenuList() {
        mBinding.framelayoutProductdetailOptionbutton.setOnClickListener {
            val visibility = mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility
            if (visibility == View.VISIBLE) {
                mBinding.framelayoutProductdetailOptionbutton.setBackgroundResource(R.drawable.border_all_whitethree)
                mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.GONE
            } else {
                mBinding.framelayoutProductdetailOptionbutton.setBackgroundResource(R.drawable.border_all_whitefour_emptybottom)
                mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.VISIBLE
            }
            mBinding.executePendingBindings()
        }

        mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.GONE
        mBinding.recyclerviewProductdetailOptionspinner.adapter = ProductDetailOptionListAdapter().apply {
            this.mList = mViewModel.product.optionInfos?.toMutableList() ?: mutableListOf()
            this.mItemClickTask = { option ->

                // close list
                mBinding.constraintlayoutProductdetailOptionspinnerlist.visibility = View.GONE
                mBinding.framelayoutProductdetailOptionbutton.setBackgroundResource(R.drawable.border_all_whitethree)

                if (option.stock > 0) {
                    mBinding.linearlayoutProductdetailOption.visibility = View.GONE
                    mBinding.imageviewProductdetailOptionselected.setBackgroundColor(Color.parseColor(option.rgb1))
                    mBinding.textviewProductdetailOptionselected.text = getOptionText(option)
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
        }
    }

    private fun getOptionText(option: OptionInfo): String {
        var optionText = ""
        if (!option.attribute1.isNullOrEmpty())
            optionText = "${option.attribute1}"

        if (!option.attribute2.isNullOrEmpty())
            optionText = "$optionText / ${option.attribute2}"

        if (!option.attribute3.isNullOrEmpty())
            optionText = "$optionText / ${option.attribute3}"

        return optionText
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