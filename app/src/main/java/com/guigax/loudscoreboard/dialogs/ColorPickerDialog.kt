package com.guigax.loudscoreboard.dialogs

import android.content.Context
import android.graphics.drawable.Drawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.guigax.loudscoreboard.adapters.ColorItemAdapter
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Color
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Color
import com.guigax.loudscoreboard.preferences.ColorOptions

class ColorPickerDialog(private val context: Context) {
    private val dialogBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
    private val iconResList = mutableListOf<Drawable>()
    private val colorNameList = mutableListOf<String>()
    var teamNumber: Int = 0

    fun clearOptions() {
        iconResList.clear()
        colorNameList.clear()
    }

    fun addOption(iconRes: Drawable, optionText: String) {
        iconResList.add(iconRes)
        colorNameList.add(optionText)
    }

    fun create(): androidx.appcompat.app.AlertDialog {
        val adapter = ColorItemAdapter(context, colorNameList, iconResList)

        dialogBuilder
            .setTitle("Pick a color")
            .setAdapter(adapter) { _, which ->
                if (teamNumber == 1) {
                    DataCoordinator.shared.updateTeam1Color(ColorOptions.colors[which])
                } else {
                    DataCoordinator.shared.updateTeam2Color(ColorOptions.colors[which])
                }

            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle cancel button click if needed
            }
        return dialogBuilder.create()
    }
}