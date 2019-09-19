package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.SearchWordEntity
import io.temco.guhada.data.model.search.SearchWord
import io.temco.guhada.data.viewmodel.SearchWordViewModel
import io.temco.guhada.databinding.ActivitySearchwordBinding
import io.temco.guhada.view.WrapContentLinearLayoutManager
import io.temco.guhada.view.activity.base.BindActivity
import java.lang.Exception
import java.util.*

/**
 * @author park jungho
 * 19.08.05
 * 상품 검색 Activity
 *
 */
class SearchWordActivity : BindActivity<ActivitySearchwordBinding>(){

    private lateinit var mViewModel: SearchWordViewModel

    private var searchWord : String = ""
    private var isNewActivity : Boolean = false

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun getBaseTag(): String = this@SearchWordActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_searchword
    override fun getViewType(): Type.View = Type.View.SEARCH_WORD

    override fun init() {
        mViewModel = SearchWordViewModel(this, mDisposable,db)
        mBinding.viewModel = mViewModel
        if(!intent.extras.isEmpty && intent.extras.containsKey("searchWord")) searchWord = intent.extras.getString("searchWord")
        if(!intent.extras.isEmpty && intent.extras.containsKey("isNewActivity")) isNewActivity = intent.extras.getBoolean("isNewActivity")
        mViewModel.isNewActivity = isNewActivity
        setViewInit()
    }


    private fun setViewInit(){
        mBinding.buttonSearchwordBack.setOnClickListener { finish() }
        mBinding.buttonSearchwordSearch.setOnClickListener { searchWordList() }
        mBinding.buttonSearchwordDeleteword.setOnClickListener { mBinding.edittextSearchwordWord.text = Editable.Factory.getInstance().newEditable("") }
        mBinding.edittextSearchwordWord.text = Editable.Factory.getInstance().newEditable(searchWord)
        mBinding.edittextSearchwordWord.setSelection(searchWord.length)

        mBinding.edittextSearchwordWord.addTextChangedListener(object  : TextWatcher{
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(::mViewModel.isInitialized) mViewModel.getAutoCompolete(s.toString())
                if(s.isNullOrEmpty() || s.isNullOrBlank()){
                    mViewModel.emptyEditTextWord.set(true)
                }else{
                    mViewModel.emptyEditTextWord.set(false)
                }
            }
        })
        mBinding.edittextSearchwordWord.setOnEditorActionListener { v, actionId, event ->
            var flag = false
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchWordList()
                flag = true
            }
            flag
        }
        mBinding.recyclerviewSearchwordRecent.setHasFixedSize(true)
        mBinding.recyclerviewSearchwordRecent.layoutManager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerviewSearchwordRecent.layoutManager as WrapContentLinearLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerviewSearchwordRecent.layoutManager as WrapContentLinearLayoutManager).recycleChildrenOnDetach = true

        mViewModel.listRecentData.observe(this,
                androidx.lifecycle.Observer<ArrayList<SearchWord>> {
                    if(it.size > 0) {
                        mViewModel.recentEmptyViewVisible.set(false)
                    }else{
                        mViewModel.recentEmptyViewVisible.set(true)
                    }
                    mViewModel.getRecentAdapter().notifyDataSetChanged()
                }
        )

        mBinding.recyclerviewSearchwordPopular.setHasFixedSize(true)
        mBinding.recyclerviewSearchwordPopular.layoutManager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerviewSearchwordPopular.layoutManager as WrapContentLinearLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerviewSearchwordPopular.layoutManager as WrapContentLinearLayoutManager).recycleChildrenOnDetach = true

        mViewModel.listPopularData.observe(this,
                androidx.lifecycle.Observer<ArrayList<SearchWord>> {
                    mViewModel.getPopularAdapter().notifyDataSetChanged()
                }
        )


        mBinding.recyclerviewSearchwordAuto.setHasFixedSize(true)
        mBinding.recyclerviewSearchwordAuto.layoutManager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerviewSearchwordAuto.layoutManager as WrapContentLinearLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerviewSearchwordAuto.layoutManager as WrapContentLinearLayoutManager).recycleChildrenOnDetach = true

        mViewModel.listAutoCompleteData.observe(this,
                androidx.lifecycle.Observer<ArrayList<SearchWord>> {
                    //mViewModel.getAutoCompleteAdapter().notifyDataSetChanged()
                }
        )

        if(searchWord.isNotEmpty()) mViewModel.emptyEditTextWord.set(false)
        CommonViewUtil.showKeyborad(mBinding.edittextSearchwordWord,this@SearchWordActivity)

    }


    private fun searchWordList(){
        var text = mBinding.edittextSearchwordWord.text.toString()
        if(!text.isNullOrEmpty() && !text.isBlank()){
            mViewModel.searchWordList(text)
        }else{
            ToastUtil.showMessage(resources.getString(R.string.search_word_nosearchwordtoast))
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            mDisposable?.dispose()
            GuhadaDB.destroyInstance()
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
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

    /*override fun onClick(v: View?) {
        when(v!!.id){
            R.id.linearlayout_searchword_recentlayout -> mViewModel.clickTab(0)
            R.id.linearlayout_searchword_popularlayout -> mViewModel.clickTab(1)
        }
    }*/
}