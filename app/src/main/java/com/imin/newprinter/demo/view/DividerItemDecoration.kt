package com.imin.newprinter.demo.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */
class DividerItemDecoration(
    private val divider: Drawable
) : RecyclerView.ItemDecoration() {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        val layoutManager = parent.layoutManager ?: return

        for (i in 0 until childCount - 1) { // Note: childCount - 1 to skip drawing after the last item
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = layoutManager.getLeftDecorationWidth(child)
            val right = layoutManager.width - layoutManager.getRightDecorationWidth(child)
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || position == state.itemCount - 1) {
            outRect.set(0, 0, 0, 0) // No offset for the last item or invalid position
        } else {
            outRect.set(0, 0, 0, divider.intrinsicHeight)
        }
    }
}
