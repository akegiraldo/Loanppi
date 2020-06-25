package com.android.loanppi

import android.content.Context
import android.graphics.Typeface
import java.util.*

class icon_manager {
    private val cachedIcons = Hashtable<String, Typeface>()

    /* Function that receive the path of icons font file and return
       the Typeface associated */
    fun <Typeface> get_icons(path: String, context: Context): android.graphics.Typeface? {
        var icons = cachedIcons.get(path)

        if (icons == null) {
            icons = android.graphics.Typeface.createFromAsset(context.assets, path)
            cachedIcons.put(path, icons)
        }

        return icons
    }
}