package io.temco.guhada.view.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentListbottomsheetBinding
import io.temco.guhada.databinding.ItemListbottomsheetBinding
import io.temco.guhada.view.holder.base.BaseViewHolder


/**
 * Text List BottomSheetFragment
 * @author Hyeyeon Park
 * @since 2019.08.30
 */
class ListBottomSheetFragment(val mContext: Context) : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentListbottomsheetBinding
    var mTitle: String = ""
    var mList = mutableListOf<String>()
    var selectedIndex = -1
    lateinit var mListener: ListBottomSheetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_listbottomsheet, container, false)
        mBinding.title = mTitle
        mBinding.imagebuttonListbottomsheetClose.setOnClickListener { dismiss() }
        mBinding.recyclerView.adapter = ListBottomSheetAdapter().apply {
            this.mList = this@ListBottomSheetFragment.mList
            this.mListener = this@ListBottomSheetFragment.mListener
            this.selectedIndex = this@ListBottomSheetFragment.selectedIndex
        }

        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        mBinding.recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, metrics.heightPixels)

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

    class ListBottomSheetAdapter : RecyclerView.Adapter<ListBottomSheetAdapter.Holder>() {
        var selectedIndex = -1
        var mList = mutableListOf<String>()
        lateinit var mListener: ListBottomSheetListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_listbottomsheet, parent, false))

        override fun getItemCount(): Int = mList.size
        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(mList[position], position)
        }

        inner class Holder(mBinding: ItemListbottomsheetBinding) : BaseViewHolder<ItemListbottomsheetBinding>(mBinding.root) {
            fun bind(text: String, position : Int) {
                mBinding.linearlayoutListbottomsheetContainer.setOnClickListener {
                    mListener.onItemClick(adapterPosition)
                    mListener.onClickClose()
                }
                mBinding.text = text
                if(selectedIndex != -1 && position == selectedIndex) mBinding.textviewListbottomsheetText.setTextColor(Color.parseColor("#5d2ed1"))
                else mBinding.textviewListbottomsheetText.setTextColor(Color.parseColor("#111111"))
                mBinding.executePendingBindings()
            }
        }
    }

    interface ListBottomSheetListener {
        fun onItemClick(position: Int)
        fun onClickClose()
    }
}