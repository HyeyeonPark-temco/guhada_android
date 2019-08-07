package io.temco.guhada.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.search.*
import io.temco.guhada.data.viewmodel.SearchWordViewModel
import io.temco.guhada.databinding.ItemEmptyListBinding
import io.temco.guhada.databinding.ItemSearchwordAutocompleteListBinding
import io.temco.guhada.databinding.ItemSearchwordPopularListBinding
import io.temco.guhada.databinding.ItemSearchwordRecentListBinding
import io.temco.guhada.view.adapter.base.CommonRecyclerAdapter
import io.temco.guhada.view.holder.base.BaseProductViewHolder
import java.util.*
import kotlin.collections.ArrayList
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableStringBuilder
import java.util.regex.Pattern

/**
 * @author park jungho
 *
 * 검색 화면에 사용하는 adapter
 */
class SearchWordAdapter (private val model : ViewModel, list : ArrayList<SearchWord>) :
        CommonRecyclerAdapter<SearchWord, SearchWordAdapter.ListViewHolder>(list){

    /**
     * HomeType 에 따른 item view
     */
    private fun getLayoutIdForPosition(position: Int): Int {
        when(items[position].type){
            SearchWordType.RECENT->return R.layout.item_searchword_recent_list
            SearchWordType.POPULAR->return R.layout.item_searchword_popular_list
            SearchWordType.AUTOCOMPLETE->return R.layout.item_searchword_autocomplete_list
            else ->return R.layout.item_empty_list
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(items[viewType].type){
            SearchWordType.RECENT->{
                val binding : ItemSearchwordRecentListBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SearchWordRecent(binding.root, binding)
            }
            SearchWordType.POPULAR->{
                val binding : ItemSearchwordPopularListBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SearchWordPopular(binding.root, binding)
            }
            SearchWordType.AUTOCOMPLETE->{
                val binding : ItemSearchwordAutocompleteListBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SearchWordAutoComplete(binding.root, binding)
            }
            else ->{
                val binding : ItemEmptyListBinding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForPosition(viewType), parent, false)
                return SearchWordEmpty(binding.root, binding)
            }
        }
    }

    override fun setOnBindViewHolder(viewHolder: ListViewHolder, item: SearchWord, position: Int) {
        viewHolder.bind(model as SearchWordViewModel, position, item)
    }

    override fun isFooter(position: Int) = false
    override fun getItemCount() = items.size

    /**
     * 메인 리스트에 사용할 base view holder
     */
    open abstract class ListViewHolder(containerView: View, binding: ViewDataBinding) : BaseProductViewHolder<ViewDataBinding>(containerView){
        abstract fun bind(viewModel : SearchWordViewModel, position : Int, item : SearchWord)
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class SearchWordRecent(private val containerView: View, val binding: ItemSearchwordRecentListBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        override fun bind(viewModel: SearchWordViewModel, position: Int, item: SearchWord) {
            if(item is SearchRecent){
                binding.keyword = item.recent.searchWord
                binding.setClickListener { v ->
                    when(v.id){
                        R.id.linearlayout_itemsearchwordrecent_list -> {
                            viewModel.searchWordList(item.recent.searchWord)
                        }
                        R.id.imagebutton_itemsearchwordrecent_delete -> {
                            viewModel.deleteRecentWord(item.recent.searchWord)
                            viewModel.getRecentAdapter().items.removeAt(position)
                            viewModel.getRecentAdapter().notifyDataSetChanged()
                        }
                    }
                }
                var cal : Calendar= Calendar.getInstance().apply {
                    timeInMillis = item.recent.insertDate
                }
                binding.date = String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "." + String.format("%02d",(cal.get(Calendar.DAY_OF_MONTH)))
            }
        }
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class SearchWordPopular(private val containerView: View, val binding: ItemSearchwordPopularListBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        override fun bind(viewModel: SearchWordViewModel, position: Int, item: SearchWord) {
            if(item is SearchPopular){
                binding.keyword = item.popular.keyword.replace("\\n"," ")
                binding.setClickListener {
                    viewModel.searchWordList(item.popular.keyword)
                }
                binding.textviewItemsearchwordpopularRank.text = (position+1).toString()
                if(position < 3){
                    binding.textviewItemsearchwordpopularRank.setTextColor(Color.parseColor("#5d2ed1"))
                }else{
                    binding.textviewItemsearchwordpopularRank.setTextColor(Color.parseColor("#777777"))
                }
                if("new".equals(item.popular.rankChange)) {
                    binding.textviewItemsearchwordpopularRankChange.text = "NEW"
                    binding.textviewItemsearchwordpopularRankChange.setTextColor(Color.parseColor("#ce2525"))
                    binding.imageviewItemsearchwordpopularRankChange.visibility = View.GONE
                }else if(item.popular.rankChange.toInt() > 0){
                    binding.textviewItemsearchwordpopularRankChange.text = item.popular.rankChange
                    binding.textviewItemsearchwordpopularRankChange.setTextColor(Color.parseColor("#ce2525"))
                    binding.imageviewItemsearchwordpopularRankChange.visibility = View.VISIBLE
                    binding.imageviewItemsearchwordpopularRankChange.setBackgroundResource(R.drawable.icon_up)
                }else{
                    binding.textviewItemsearchwordpopularRankChange.text = item.popular.rankChange
                    binding.textviewItemsearchwordpopularRankChange.setTextColor(Color.parseColor("#0058dd"))
                    binding.imageviewItemsearchwordpopularRankChange.setBackgroundResource(R.drawable.icon_down)
                    binding.imageviewItemsearchwordpopularRankChange.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class SearchWordAutoComplete(private val containerView: View, val binding: ItemSearchwordAutocompleteListBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        override fun bind(viewModel: SearchWordViewModel, position: Int, item: SearchWord) {
            if(item is SearchAutoComplete){
                val sb = SpannableStringBuilder(item.name)
                val p = Pattern.compile(item.searchWord, Pattern.CASE_INSENSITIVE)
                val m = p.matcher(item.name)
                while (m.find()) {
                    sb.setSpan(ForegroundColorSpan(Color.parseColor("#5d2ed1")), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
                binding.textviewItemsearchwordautocompleteName.text = sb
                binding.setClickListener {
                    viewModel.searchWordList(item.name)
                }
            }
        }
    }

    /**
     * 메인 리스트에 event viewpager view holder
     */
    inner class SearchWordEmpty(private val containerView: View, val binding: ItemEmptyListBinding) : ListViewHolder(containerView, binding){
        override fun init(context: Context?, manager: RequestManager?, data: Deal?) { }
        override fun bind(viewModel: SearchWordViewModel, position: Int, item: SearchWord) { }
    }


}
