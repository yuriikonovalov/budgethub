package com.yuriikonovalov.common.framework.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class AutoGridRecyclerView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) :
    RecyclerView(ctx, attrs, defStyleAttr) {
    private lateinit var layoutManager: GridLayoutManager
    private var columnWidth = -1

    init {
        initialization(ctx, attrs)
    }

    private fun initialization(context: Context, attrs: AttributeSet?) {
        try {
            if (attrs != null) {
                val attrsArray = intArrayOf(android.R.attr.columnWidth)

                val typedArray = context.obtainStyledAttributes(attrs, attrsArray)
                // Index is 0 because the attrsArray has only one element.
                columnWidth = typedArray.getDimensionPixelSize(0, -1)
                typedArray.recycle()
            }

            layoutManager = GridLayoutManager(context, 1)
            setLayoutManager(layoutManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        try {
            if (columnWidth > 0) {
                val spanCount = max(1, measuredWidth / columnWidth)
                layoutManager.spanCount = spanCount
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}