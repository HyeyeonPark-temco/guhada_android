package io.temco.guhada.view.fragment.cart

import android.app.Activity
import android.view.View
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.ActivityMoveToMain
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.viewmodel.cart.EmptyCartViewModel
import io.temco.guhada.databinding.FragmentEmptycartBinding
import io.temco.guhada.view.adapter.cart.AddCartResultProductAdapter
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType

class EmptyCartFragment : BaseFragment<FragmentEmptycartBinding>() ,View.OnClickListener{
    private lateinit var mViewModel: EmptyCartViewModel

    override fun getBaseTag(): String = EmptyCartFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_emptycart

    override fun init() {
        initViewModel()
        mViewModel.getDeals()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()

        mBinding.setOnClickContinue {
            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true)
            (context as Activity).setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
            (context as Activity).onBackPressed()
        }

        mBinding.setOnClickBookmark {
            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_MYPAGE.flag, MyPageTabType.BOOKMARK.ordinal, true)
            (context as Activity).setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
            (context as Activity).onBackPressed()
        }

    }


    private fun initViewModel() {
        mViewModel = EmptyCartViewModel()
        mViewModel.mDealList.observe(this, Observer {
            mBinding.recyclerviewCartRecommend.adapter = AddCartResultProductAdapter().apply {
                this.mList = it
                this.mClickItemTask = {dealId ->
                    CommonUtil.startProductActivity(this@EmptyCartFragment.context as Activity, dealId)
                    (this@EmptyCartFragment.context as Activity).finish()
                }
            }
        })

    }

    override fun onClick(v: View?) {
        /*when (v?.getId()) {
            R.id.layout_maintab_layout1 -> showCategoryListDialog()

            R.id.layout_maintab_layout2 -> showBrandListDialog()

            R.id.layout_maintab_layout3 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_HOME, true)
                (context as ProductFilterListActivity).setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
                (context as ProductFilterListActivity).finish()
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
            }


            R.id.layout_maintab_layout4 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_COMUNITY, true)
                (context as ProductFilterListActivity).setResult(Flag.ResultCode.GO_TO_MAIN_COMUNITY)
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
                (context as ProductFilterListActivity).finish()
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
            }

            R.id.layout_maintab_layout5 -> {
                BaseApplication.getInstance().moveToMain = ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, true)
                (context as ProductFilterListActivity).setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE)
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
                (context as ProductFilterListActivity).finish()
                (context as ProductFilterListActivity).overridePendingTransition(R.anim.fade, R.anim.fade)
            }
        }//                Toast.makeText(getContext(), getString(R.string.common_message_ing), Toast.LENGTH_SHORT).show();*/
    }
}