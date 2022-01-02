package com.s26462.shoppingmanagment.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.s26462.shoppingmanagment.R

abstract class SwipeToEditCallback(context: Context):
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white)
        private val intrinsicWidth = editIcon!!.intrinsicWidth
        private val intrinsicHeight = editIcon!!.intrinsicHeight
        private val background = ColorDrawable()
        private val backgroundColor = R.color.shop_edit_color
        private val clearPaint = Paint().apply {xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)}
    }

// TODO edycja sklepów