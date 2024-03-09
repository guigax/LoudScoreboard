package com.guigax.loudscoreboard.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.guigax.loudscoreboard.R

class ColorPickerDialog : DialogFragment() {

    interface ColorPickerListener {
        fun onColorSelected(color: Int)
    }

    companion object {
        val colorOptions = arrayOf(
            android.R.color.holo_blue_light,
            android.R.color.holo_purple,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
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

        // Set up color circles and click listeners
        for (i in colorOptions.indices) {
            // TODO: change into a custom component
            val colorCircle = dialogView.findViewById<ImageView>(
                resources.getIdentifier(
                    "color_circle_$i",
                    "id",
                    context?.packageName
                )
            )
            val circle =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_filled) } as Drawable
            val fillColor =
                context?.let { ContextCompat.getColor(it, colorOptions[i]) }
            circle.colorFilter =
                fillColor?.let { PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
            colorCircle.setImageDrawable(circle)
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
