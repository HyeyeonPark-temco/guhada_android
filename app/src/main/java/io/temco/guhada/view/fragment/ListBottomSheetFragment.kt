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
import io.temco.guhada.databinding.FragmentSellerstoreMoreBinding
import io.temco.guhada.databinding.ItemListbottomsheetBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ListBottomSheetFragment(val mContext: Context) : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentSellerstoreMoreBinding
    var mList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sellerstore_more, container, false)
        mBinding.recyclerView.adapter = ListBottomSheetAdapter().apply { this.mList = this@ListBottomSheetFragment.mList }
        mBinding.executePendingBindings()
        return mBinding.root
    }

    class ListBottomSheetAdapter : RecyclerView.Adapter<ListBottomSheetAdapter.Holder>() {
        var mList = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_listbottomsheet, parent, false))

        override fun getItemCount(): Int = mList.size
        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(mList[position])
        }

        inner class Holder(mBinding: ItemListbottomsheetBinding) : BaseViewHolder<ItemListbottomsheetBinding>(mBinding.root) {
            fun bind(text: String) {
                mBinding.text = text
                mBinding.executePendingBindings()
            }
        }
    }
}