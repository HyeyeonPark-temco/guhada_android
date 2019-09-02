package io.temco.guhada.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var mListener: ListBottomSheetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_listbottomsheet, container, false)
        mBinding.title = mTitle
        mBinding.imagebuttonListbottomsheetClose.setOnClickListener { dismiss() }
        mBinding.recyclerView.adapter = ListBottomSheetAdapter().apply {
            this.mList = this@ListBottomSheetFragment.mList
            this.mListener = this@ListBottomSheetFragment.mListener
        }
        mBinding.executePendingBindings()
        return mBinding.root
    }

    class ListBottomSheetAdapter : RecyclerView.Adapter<ListBottomSheetAdapter.Holder>() {
        var mList = mutableListOf<String>()
        lateinit var mListener: ListBottomSheetListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_listbottomsheet, parent, false))

        override fun getItemCount(): Int = mList.size
        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(mList[position])
        }

        inner class Holder(mBinding: ItemListbottomsheetBinding) : BaseViewHolder<ItemListbottomsheetBinding>(mBinding.root) {
            fun bind(text: String) {
                mBinding.linearlayoutListbottomsheetContainer.setOnClickListener {
                    mListener.onItemClick(adapterPosition)
                    mListener.onClickClose()
                }
                mBinding.text = text
                mBinding.executePendingBindings()
            }
        }
    }

    interface ListBottomSheetListener {
        fun onItemClick(position: Int)
        fun onClickClose()
    }
}