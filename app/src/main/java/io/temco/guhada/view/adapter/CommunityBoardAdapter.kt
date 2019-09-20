package io.temco.guhada.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.viewmodel.CommunitySubListViewModel
import io.temco.guhada.databinding.ItemCommunityPhotoBinding
import io.temco.guhada.databinding.ItemCommunityTextBinding
import io.temco.guhada.view.fragment.community.CommunitySubListFragment
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 커뮤니티 게시판 어댑터
 * @author Hyeyeon Park
 */
class CommunityBoardAdapter(val type: String) : RecyclerView.Adapter<CommunityBoardAdapter.Holder>() {
    var mList = mutableListOf<CommunityBoard>()
    lateinit var mViewModel: CommunitySubListViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
                if (type == CommunitySubListFragment.CommunityListType.IMAGE.type)
                    DataBindingUtil.inflate<ItemCommunityPhotoBinding>(LayoutInflater.from(parent.context), R.layout.item_community_photo, parent, false)
                else
                    DataBindingUtil.inflate<ItemCommunityTextBinding>(LayoutInflater.from(parent.context), R.layout.item_community_text, parent, false)
        return Holder(binding, mViewModel, type)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    class Holder(binding: ViewDataBinding, val mViewModel: CommunitySubListViewModel, val type: String) : BaseViewHolder<ViewDataBinding>(binding.root) {
        fun bind(item: CommunityBoard) {
            Log.e("ㅇㅇㅇ", item.newlyCreated.toString())
            if (type == CommunitySubListFragment.CommunityListType.IMAGE.type) {
                (mBinding as ItemCommunityPhotoBinding).item = item
                (mBinding as ItemCommunityPhotoBinding).viewModel = mViewModel
                //checkEllipsized(isTextType = false)
            } else {
                (mBinding as ItemCommunityTextBinding).item = item
                (mBinding as ItemCommunityTextBinding).viewModel = mViewModel
                //checkEllipsized(isTextType = true)
            }

            setSpacing()
            mBinding.executePendingBindings()
        }

        private fun checkEllipsized(isTextType: Boolean) {
            val titleTextView: TextView
            val margin: Int

            if (isTextType) {
                titleTextView = (mBinding as ItemCommunityTextBinding).textviewCommunitytextTitle
                margin = CommonViewUtil.convertDpToPixel(5, mBinding.root.context)
            } else {
                titleTextView = (mBinding as ItemCommunityPhotoBinding).textviewCommunityphotoTitle
                margin = CommonViewUtil.convertDpToPixel(16, mBinding.root.context)
            }

            titleTextView.viewTreeObserver.addOnGlobalLayoutListener {
                if (titleTextView.layout != null) {
                    val isEllipsized = titleTextView.layout.getEllipsisCount(titleTextView.lineCount - 1) > 0

                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        if (isEllipsized) {
                            weight = 1f
                            titleTextView.viewTreeObserver.removeOnGlobalLayoutListener { }
                        }

                        if (isTextType) marginStart = margin
                        else topMargin = margin
                    }.let { layoutParams ->
                        titleTextView.layoutParams = layoutParams
                    }
                }
            }
        }

        private fun setSpacing() {
            if (type == CommunitySubListFragment.CommunityListType.IMAGE.type) {
                ((mBinding as ItemCommunityPhotoBinding).constraintlayoutCommunityphotoContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    if (adapterPosition % 2 == 0) {
                        leftMargin = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
                        rightMargin = CommonViewUtil.convertDpToPixel(dp = 5, context = mBinding.root.context)
                    } else {
                        leftMargin = CommonViewUtil.convertDpToPixel(dp = 5, context = mBinding.root.context)
                        rightMargin = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
                    }
                }.let {
                    (mBinding as ItemCommunityPhotoBinding).constraintlayoutCommunityphotoContainer.layoutParams = it
                }
            }
        }
    }
}