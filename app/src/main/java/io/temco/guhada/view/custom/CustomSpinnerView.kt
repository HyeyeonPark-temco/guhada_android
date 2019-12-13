package io.temco.guhada.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.databinding.ItemCustomspinnerBinding
import io.temco.guhada.databinding.LayoutCustomspinnerBinding

/**
 * @author Hyeyeon Park
 */
class CustomSpinnerView : LinearLayout {
    private lateinit var mBinding: LayoutCustomspinnerBinding
    private lateinit var mPopup: ListPopupWindow
    private lateinit var mAdapter: CustomSpinnerAdapter
    private var mList = mutableListOf<Any>()
    private var mOnItemClickTask: (position: Int) -> Unit = {}
    private var mSelectedRgb = ""

    // attrs
    private var mPlaceHolder = "select"
    private var mItemHeight = 0
    private var mMaxVisibleCount = 0
    private var mIsLarge = false
    private var mIsRgb = false

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_customspinner, this, true)
        mPopup = ListPopupWindow(context)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSpinnerView)
        mPlaceHolder = typedArray.getString(R.styleable.CustomSpinnerView_placeHolder) ?: "select"
        mIsLarge = typedArray.getBoolean(R.styleable.CustomSpinnerView_isLarge, false)
        val defaultItemHeight = if (mIsLarge) context.resources.getDimensionPixelSize(R.dimen.height_spinner_large) else context.resources.getDimensionPixelSize(R.dimen.height_spinner_default)
        mItemHeight = typedArray.getDimensionPixelOffset(R.styleable.CustomSpinnerView_itemHeight, CommonViewUtil.convertPixelToDp(context = context, px = defaultItemHeight))
        mMaxVisibleCount = typedArray.getInteger(R.styleable.CustomSpinnerView_maxVisibleCount, 0)

        mBinding.textviewCustomspinnerPlaceholder.text = mPlaceHolder
        mBinding.textviewCustomspinnerPlaceholder.layoutParams.height = CommonViewUtil.dipToPixel(context = context, dip = mItemHeight)
        mPopup.anchorView = mBinding.motionlayoutCustomspinner
        mPopup.isModal = true

        mPopup.setBackgroundDrawable(context.getDrawable(R.drawable.background_spinner_listpopup))

        mBinding.textviewCustomspinnerPlaceholder.setOnClickListener {
            if (mPopup.isShowing) dismiss()
            else show()
        }

        mPopup.setOnItemClickListener { parent, view, position, id ->
            if (mList.size > position) {
                val item: Any = mList[position]
                if (item is String) mBinding.textviewCustomspinnerPlaceholder.text = item
                mOnItemClickTask(position)
            }

            dismiss()
        }

        mPopup.setOnDismissListener {
            mBinding.motionlayoutCustomspinner.transitionToStart()
        }


        mBinding.executePendingBindings()
        typedArray.recycle()
    }

    fun setItems(list: MutableList<Any>) {
        this.mList = list
        if (list.isNotEmpty() && list[0] is String) {
            mAdapter = CustomSpinnerAdapter(list = list as MutableList<String>)
            mPopup.setAdapter(mAdapter)
            mPopup.listView?.overScrollMode = View.OVER_SCROLL_NEVER
            setListHeight(list.size)
        }
    }

    fun setAdapter(adapter: BaseAdapter) {
        if (adapter.count > 0)
            for (i in 0 until adapter.count)
                mList.add(adapter.getItem(i))

        mPopup.setAdapter(adapter)
        setListHeight(adapter.count)
    }

    private fun setListHeight(itemCount: Int) {
        val count =
                if (mMaxVisibleCount > 0 && itemCount > mMaxVisibleCount) mMaxVisibleCount
                else itemCount

        val height = CommonViewUtil.convertDpToPixel(context = context, dp = mItemHeight) * count
        mPopup.height = height + (mPopup.listView?.dividerHeight ?: 0 * count)
    }

    fun setOnItemClickTask(task: (position: Int) -> Unit) {
        this.mOnItemClickTask = task
    }

    fun setPlaceHolder(placeHolder: String) {
        this.mPlaceHolder = placeHolder
        mBinding.textviewCustomspinnerPlaceholder.text = mPlaceHolder
    }

    fun setRgb(rgb: String?) {
        if (rgb?.isNotEmpty() == true) {
            mSelectedRgb = rgb
            mBinding.imageviewCustomspinnerRgb.setBackgroundColor(Color.parseColor(rgb))
            if (mBinding.textviewCustomspinnerPlaceholder.paddingLeft == 0)
                mBinding.textviewCustomspinnerPlaceholder.setPadding(CommonViewUtil.convertDpToPixel(32, context), 0, 0, 0)
        } else {
            mBinding.textviewCustomspinnerPlaceholder.setPadding(0, 0, 0, 0)
        }
    }

    private fun show() {
        mBinding.motionlayoutCustomspinner.transitionToEnd()
        mPopup.show()
    }

    private fun dismiss() {
        mBinding.motionlayoutCustomspinner.transitionToStart()
        mPopup.dismiss()
    }

    inner class CustomSpinnerAdapter(val list: MutableList<String>) : BaseAdapter() {
        private lateinit var mBinding: ItemCustomspinnerBinding

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_customspinner, parent, false)
            mBinding.textviewCustomspinnerList.text = list[position]
            (mBinding.textviewCustomspinnerList.layoutParams as LinearLayout.LayoutParams).height = mItemHeight
            return mBinding.root
        }

        override fun getItem(position: Int): Any = list[position]
        override fun getItemId(position: Int): Long = 0
        override fun getCount(): Int = list.size
    }


}