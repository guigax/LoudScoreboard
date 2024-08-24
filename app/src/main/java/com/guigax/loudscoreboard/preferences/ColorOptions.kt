package com.guigax.loudscoreboard.preferences

import android.content.Context
import com.guigax.loudscoreboard.R

object ColorOptions {
    val colors = arrayOf(
        android.R.color.holo_blue_light,
        android.R.color.holo_purple,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light,
    )

    fun colorNamesFromContext(context: Context): Array<String> {
        return arrayOf(
            context.getString(R.string.color_light_blue),
            context.getString(R.string.color_purple),
            context.getString(R.string.color_light_green),
            context.getString(R.string.color_light_orange),
            context.getString(R.string.color_light_red),
        )
    }
}