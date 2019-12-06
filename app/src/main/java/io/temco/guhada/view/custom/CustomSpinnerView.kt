package io.temco.guhada.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.databinding.ItemCustomspinnerBinding
import io.temco.guhada.databinding.LayoutCustomspinnerBinding

/**
 * @author Hyeyeon Park
 */
class CustomSpinnerView : LinearLayout {
    private lateinit var mBinding: LayoutCustomspinnerBinding
    private var mList = mutableListOf<String>()
    private lateinit var mPopup: ListPopupWindow
    private var mOnItemClickTask: (position: Int) -> Unit = {}
    private lateinit var mAdapter: CustomSpinnerAdapter

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
        val placeHolder = typedArray.getString(R.styleable.CustomSpinnerView_placeHolder)
                ?: "select"
//        val height = typedArray.getDimension(R.styleable.CustomSpinnerView_android_layout_height, CommonViewUtil.convertDpToPixel(45, context).toFloat())
//        (mBinding.textviewCustomspinnerPlaceholder.layoutParams as ConstraintLayout.LayoutParams).height = height.toInt()

        mBinding.textviewCustomspinnerPlaceholder.text = placeHolder
        mPopup.anchorView = mBinding.textviewCustomspinnerPlaceholder
        mPopup.isModal = true

//        mPopup.setBackgroundDrawable(context.getDrawable(R.drawable.background_listpopup))

        mBinding.textviewCustomspinnerPlaceholder.setOnClickListener {
            if (mPopup.isShowing) dismiss()
            else show()
        }

        mPopup.setOnItemClickListener { parent, view, position, id ->
            mBinding.textviewCustomspinnerPlaceholder.text = mList[position]
            dismiss()
            mOnItemClickTask(position)
        }

        mPopup.setOnDismissListener {
            mBinding.motionlayoutCustomspinner.transitionToStart()
        }

        mBinding.executePendingBindings()
        typedArray.recycle()
    }

    fun setItems(list: MutableList<String>) {
        this.mList = list
        mAdapter = CustomSpinnerAdapter(list = mList)
        mPopup.setAdapter(mAdapter)
    }

    fun setOnItemClickTask(task: (position: Int) -> Unit) {
        this.mOnItemClickTask = task
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
            return mBinding.root
        }

        override fun getItem(position: Int): Any = list[position]
        override fun getItemId(position: Int): Long = 0
        override fun getCount(): Int = list.size
    }

}