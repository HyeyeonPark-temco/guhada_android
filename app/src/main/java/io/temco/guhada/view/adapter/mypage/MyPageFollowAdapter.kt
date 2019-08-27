package io.temco.guhada.view.adapter.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import io.temco.guhada.R
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.util.GlideApp
import io.temco.guhada.data.model.BookMarkCountResponse
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.databinding.ItemMypageFollowBinding
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 마이페이지 팔로우한 스토어 어댑터
 * @author Hyeyeon Park
 * @since 2019.08.26
 */
class MyPageFollowAdapter : RecyclerView.Adapter<MyPageFollowAdapter.Holder>() {
    var mList = mutableListOf<Seller>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypage_follow, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: MutableList<Seller>) {
        this.mList = list
        notifyDataSetChanged()
    }

    fun addAllItems(items: MutableList<Seller>, startPos: Int, endPos: Int) {
        mList.addAll(items)
        notifyItemRangeInserted(startPos, endPos)
    }

    inner class Holder(binding: ItemMypageFollowBinding) : BaseViewHolder<ItemMypageFollowBinding>(binding.root) {
        fun bind(seller: Seller) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val sellerId = seller.id
                getSeller(sellerId)
                getFollowerCount(sellerId)
            }
        }

        private suspend fun getSeller(sellerId: Long) = UserServer.getSellerByIdAsync(sellerId).await().let {
            val item = it.data as Seller
            mBinding.seller = item
            mBinding.executePendingBindings()

            if (item.user.profileImageUrl.isEmpty()) {
                val drawable = mBinding.root.context.resources.getDrawable(R.drawable.background_color_search)
                GlideApp.with(mBinding.root.context).load(drawable).thumbnail(0.9f).apply(RequestOptions.circleCropTransform()).into(mBinding.imageviewMypagefollowProfile)
            } else {
                GlideApp.with(mBinding.root.context).load(item.user.profileImageUrl).thumbnail(0.9f).apply(RequestOptions.circleCropTransform()).into(mBinding.imageviewMypagefollowProfile)
            }
            Log.e("셀러 정보", "${item.id}  ${item.user.profileImageUrl}")
        }

        private suspend fun getFollowerCount(sellerId: Long) = UserServer.getBookMarkCountAsync(target = BookMarkTarget.SELLER.target, targetId = sellerId).await().let {
            val item = it.data as BookMarkCountResponse
            mBinding.textviewMypagefollowFollowcount.text = String.format(mBinding.root.context.getString(R.string.mypagefollow_followcount), item.bookmarkCount)
            Log.e("셀러 팔로우 카운트", "${item.bookmarkCount}")
        }
    }
}

