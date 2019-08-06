package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.SearchWordEntity
import io.temco.guhada.data.viewmodel.SearchWordViewModel
import io.temco.guhada.databinding.ActivitySearchwordBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.util.*

/**
 * @author park jungho
 * 19.08.05
 * 상품 검색 Activity
 *
 */
class SearchWordActivity : BindActivity<ActivitySearchwordBinding>(){

    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: SearchWordViewModel

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun getBaseTag(): String = this@SearchWordActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_searchword
    override fun getViewType(): Type.View = Type.View.SEARCH_WORD

    override fun init() {
        mViewModel = SearchWordViewModel()
        mBinding.viewModel = mViewModel

        setViewInit()
    }


    private fun setViewInit(){
        mBinding.buttonSearchwordBack.setOnClickListener { finish() }
        mBinding.buttonSearchwordSearch.setOnClickListener { searchWordList() }
    }


    private fun searchWordList(){
        var text = mBinding.edittextSearchwordWord.text.toString()
        if(!text.isNullOrEmpty() && !text.isBlank()){
            mDisposable.add(Observable.just(text).subscribeOn(Schedulers.io()).subscribe {
                db.searchWordDao().delete(text)
                var searchWordEntity = SearchWordEntity()
                searchWordEntity.initData(Calendar.getInstance().timeInMillis, text, "", "", "", "")
                if (CustomLog.flag) CustomLog.L("searchWordList", searchWordEntity.toString())
                db.searchWordDao().insert(searchWordEntity)
                var list = db.searchWordDao().getAll(21)
                if (CustomLog.flag) CustomLog.L("searchWordList list", list.toString())
                if (list.size >= 21) {
                    db.searchWordDao().delete(list[list.size - 1])
                }
            })
            var intent = Intent(this@SearchWordActivity, ProductFilterListActivity::class.java)
            intent.putExtra("type", Type.ProductListViewType.SEARCH)
            intent.putExtra("search_word", text)
            this.startActivityForResult(intent, Flag.RequestCode.BASE)
            setResult(Activity.RESULT_FIRST_USER)
            overridePendingTransition(0,0)
            finish()
        }else{
            ToastUtil.showMessage(resources.getString(R.string.search_word_nosearchwordtoast))
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable?.dispose()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
}