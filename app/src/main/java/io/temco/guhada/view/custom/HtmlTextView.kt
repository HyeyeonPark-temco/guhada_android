package io.temco.guhada.view.custom

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import android.text.Html
import io.temco.guhada.R

/**
 * @author park jungho
 *
 * html text 를 보여 주는 Custom TextView
 *
 * app:isHtml="true"
 */
class HtmlTextView : TextView{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) :super(context, attrs) {
        setHtmlText(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        setHtmlText(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    fun setHtmlText(context: Context?, attrs: AttributeSet?){
        val a = context?.obtainStyledAttributes(attrs, R.styleable.HtmlTextView, 0, 0)
        try {
            a?.let {
                val isHtml = a.getBoolean(R.styleable.HtmlTextView_isHtml, false)
                if (isHtml) {
                    val text = a.getString(R.styleable.HtmlTextView_android_text)
                    if (text != null) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT))
                        } else {
                            setText(Html.fromHtml(text))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            a?.let {it.recycle()}
        }
    }
}