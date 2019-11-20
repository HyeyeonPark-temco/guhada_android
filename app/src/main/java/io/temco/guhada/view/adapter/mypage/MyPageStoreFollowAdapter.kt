package io.temco.guhada.view.adapter.mypage

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.seller.SellerStore
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.mypage.MyPageFollowViewModel
import io.temco.guhada.databinding.ItemMypageFollowBinding
import io.temco.guhada.view.activity.SellerInfoActivity
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 마이페이지 팔로우한 스토어 어댑터
 * @author Hyeyeon Park
 * @since 2019.10.14
 */
class MyPageStoreFollowAdapter : RecyclerView.Adapter<MyPageStoreFollowAdapter.Holder>() {
    lateinit var mViewModel: MyPageFollowViewModel
    var mList = mutableListOf<SellerStore>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypage_follow, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: MutableList<SellerStore>) {
        this.mList = list
        notifyDataSetChanged()
    }

    fun addAllItems(items: MutableList<SellerStore>, startPos: Int, endPos: Int) {
        mList.addAll(items)
        notifyItemRangeInserted(startPos, endPos)
    }

    inner class Holder(binding: ItemMypageFollowBinding) : BaseViewHolder<ItemMypageFollowBinding>(binding.root) {
        fun bind(sellerStore: SellerStore) {
            val scope = CoroutineScope(Dispatchers.Main)
            mBinding.buttonMypagefollowFollow.setOnClickListener {
                scope.launch {
                    val token = Preferences.getToken()
                    if (token != null && !token.accessToken.isNullOrEmpty()) {
                        if (sellerStore.isFollowing) deleteBookMark(accessToken = "Bearer ${token.accessToken}", target = BookMarkTarget.SELLER.target, targetId = sellerStore.sellerId)
                        else saveBookMark(accessToken = "Bearer $token", bookMarkResponse = BookMarkResponse(target = BookMarkTarget.SELLER.target, targetId = sellerStore.sellerId))

                        sellerStore.isFollowing = !sellerStore.isFollowing
                    }
                }
            }

            mBinding.imageviewMypagefollowProfile.setOnClickListener {
                val intent = Intent(mBinding.root.context, SellerInfoActivity::class.java)
                intent.putExtra("sellerId", sellerStore.sellerId)
                mBinding.root.context.startActivity(intent)
            }

            if (::mViewModel.isInitialized)
                mBinding.viewModel = mViewModel

            mBinding.sellerStore = sellerStore
            mBinding.executePendingBindings()
        }

        private suspend fun saveBookMark(accessToken: String, bookMarkResponse: BookMarkResponse) {
            UserServer.saveBookMarkAsync(accessToken = accessToken, response = bookMarkResponse.getProductBookMarkRespose()).await().let {
                if (it.resultCode == ResultCode.SUCCESS.flag) {
                    val context = mBinding.root.context
                    mBinding.buttonMypagefollowFollow.background = mBinding.root.context.getDrawable(R.drawable.border_all_whitethree)
                    mBinding.buttonMypagefollowFollow.setTextColor(context.resources.getColor(R.color.black_four))
                    mBinding.buttonMypagefollowFollow.text = mBinding.root.context.getString(R.string.mypagefollow_button_following)
                } else {
                    ToastUtil.showMessage(it.message)
                }
            }
        }

        private suspend fun deleteBookMark(accessToken: String, target: String, targetId: Long) {
            UserServer.deleteBookMarkAsync(accessToken = accessToken, target = target, targetId = targetId).await().let {
                if (it.resultCode == ResultCode.SUCCESS.flag) {
                    val context = mBinding.root.context
                    mBinding.buttonMypagefollowFollow.background = context.getDrawable(R.drawable.background_color_purple)
                    mBinding.buttonMypagefollowFollow.setTextColor(Color.WHITE)
                    mBinding.buttonMypagefollowFollow.text = mBinding.root.context.getString(R.string.mypagefollow_button_follow)
                } else {
                    ToastUtil.showMessage(it.message)
                }
            }
        }
    }
}

