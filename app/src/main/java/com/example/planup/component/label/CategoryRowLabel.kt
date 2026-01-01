package com.example.planup.component.label

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.planup.R
import androidx.core.content.withStyledAttributes

class CategoryRowLabel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvLabel: TextView

    init {
        // (선택) Preview 안정화: 필요하면 사용
        // if (isInEditMode) {
        //     tvLabel = TextView(context).apply { text = "Category(Preview)" }
        //     addView(tvLabel)
        //     return
        // }

        // item_category_button.xml이 <merge>이므로 this에 직접 붙는 구조가 맞음
        LayoutInflater.from(context).inflate(R.layout.item_category_button, this, true)

        tvLabel = findViewById(R.id.categoryText)

        context.withStyledAttributes(attrs, R.styleable.CategoryRowLabel, defStyleAttr, 0) {
            tvLabel.text = getString(R.styleable.CategoryRowLabel_labelText) ?: tvLabel.text
        }
    }
}