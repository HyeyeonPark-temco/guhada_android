package io.temco.guhada.common.util


import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView


object CommonViewUtil{

    fun dimenToValue(ctx : Context, minusDimen : Int) : Int {
        val matrix = DisplayMetrics()
        (ctx as Activity).windowManager.defaultDisplay.getMetrics(matrix)
        var px = convertDpToPixel (minusDimen,ctx)
        return (matrix.widthPixels - px)
    }

    fun dipToPixel(context: Context, dip: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun convertDpToPixel(dp: Int, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }


    fun pixelTodip(context: Context, px: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), context.resources.displayMetrics)
                .toInt() /*(px / context.getResources().getDisplayMetrics().density);*/
    }

    fun setEditTextMaxLength(editText: EditText, length: Int) {
        val FilterArray = arrayOfNulls<InputFilter>(1)
        FilterArray[0] = InputFilter.LengthFilter(length)
        editText.filters = FilterArray
    }


    fun showKeyborad(v: View, context: Context) {
        if (v is EditText) {
            (v as EditText).isFocusable = true
            (v as EditText).requestFocus()
        }
        v.postDelayed(Runnable {
            val imgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //imgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
            imgr.showSoftInput(v, 0)
        }, 300)
    }


    fun hideKeyborad(v: View, context: Context) {
        v.postDelayed(Runnable {
            val imgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }, 300)
    }

    fun textViewAddBold(txt : TextView) : TextView {
        txt.typeface = Typeface.DEFAULT_BOLD
        return txt
    }

    fun textViewRemoveBold(txt : TextView) : TextView {
        txt.typeface = Typeface.DEFAULT
        return txt
    }

    fun getFindView(context:Context, view:View, name:String) : View{
        return view.findViewById(context!!.resources.getIdentifier(name,"id",context.packageName))
    }

    fun setTextViewImageTextEnd(context: Context, res : Int, text : String, textview : TextView){
        val image = context.resources.getDrawable(res, null)
        image.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
        var span = SpannableStringBuilder(text+"n")
        span.setSpan(ImageSpan(image), text.length, text.length+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textview.text = span
    }

}