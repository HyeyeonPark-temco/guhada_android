package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import io.temco.guhada.R
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.databinding.ItemImagepagerBinding

/**
 * Image object view page adapter
 * @author Hyeyeon Park
 */
class ImagePagerAdapter : PagerAdapter() {
    var list: MutableList<String> = ArrayList()
    var mImageClickTask : () -> Unit = {}

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    override fun getCount(): Int = list.size
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil.inflate<ItemImagepagerBinding>(LayoutInflater.from(container.context), R.layout.item_imagepager, container, false)
        binding.url = list[position]
        binding.imageviewImagepager.setOnClickListener { mImageClickTask() }
        binding.executePendingBindings()
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun setItems(list: MutableList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.list.clear()
        notifyDataSetChanged()
    }
}