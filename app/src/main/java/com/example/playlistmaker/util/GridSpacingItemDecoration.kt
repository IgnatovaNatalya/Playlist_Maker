package com.example.playlistmaker.util

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int, // Количество колонок
    private val spacing: Int,    // Расстояние между элементами (в пикселях)
    private val includeEdge: Boolean // Добавлять отступы по краям?
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // Позиция элемента
        val column = position % spanCount // Номер колонки



        if (includeEdge) {
            // Отступы для крайних элементов
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) { // Верхний отступ для первой строки
                outRect.top = spacing
            }
            outRect.bottom = spacing // Нижний отступ для всех элементов
        } else {
            // Без отступов по краям RecyclerView
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}