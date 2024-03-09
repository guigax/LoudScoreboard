package com.guigax.loudscoreboard.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.guigax.loudscoreboard.R

class ColorPickerDialog : DialogFragment() {

    interface ColorPickerListener {
        fun onColorSelected(color: Int)
    }

    private var listener: ColorPickerListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the host Fragment implements the ColorPickerListener interface
        /*listener = context as? ColorPickerListener
        if (listener == null) {
            throw ClassCastException("$context must implement ColorPickerListener")
        }*/
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val colorOptions = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)

        // Set up color circles and click listeners
        for (i in colorOptions.indices) {
            val colorCircle = dialogView.findViewById<ImageView>(
                resources.getIdentifier(
                    "color_circle_$i",
                    "id",
                    context?.packageName
                )
            )
            colorCircle.setColorFilter(colorOptions[i])
            colorCircle.setOnClickListener {
                listener?.onColorSelected(colorOptions[i])
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Pick a color")
            .create()
    }
}
