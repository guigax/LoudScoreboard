package com.guigax.loudscoreboard.fragments

import ImageAdapter
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guigax.loudscoreboard.R
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.getIsMuted
import com.guigax.loudscoreboard.datacoordinator.getTeam1ColorDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2ColorDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.updateIsMuted
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Name
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Name
import com.guigax.loudscoreboard.dialogs.ColorPickerDialog
import com.guigax.loudscoreboard.preferences.ColorOptions
import kotlinx.coroutines.runBlocking

class SettingsFragment : BottomSheetDialogFragment() {
    private lateinit var team1ColorV: Button
    private lateinit var team2ColorV: Button
    private lateinit var team1NameV: EditText
    private lateinit var team2NameV: EditText
    private lateinit var dialog: ColorPickerDialog
    private lateinit var mute: CheckBox

    private var team1CurrentName = ""
    private var team2CurrentName = ""

    private var team1CurrentColor = android.R.color.holo_blue_light
    private var team2CurrentColor = android.R.color.holo_purple

    private var isMuted = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.settings_layout, container, false)

        val spinner: Spinner = fragmentView.findViewById(R.id.sound)
        val images = arrayOf(R.drawable.whistle, R.drawable.ding, R.drawable.horn)
        val adapter = ImageAdapter(fragmentView.context, images)
        spinner.adapter = adapter

        team1ColorV = fragmentView.findViewById(R.id.team1Color)
        team2ColorV = fragmentView.findViewById(R.id.team2Color)
        team1NameV = fragmentView.findViewById(R.id.team1Name)
        team2NameV = fragmentView.findViewById(R.id.team2Name)
        mute = fragmentView.findViewById(R.id.mute)

        dialog = context?.let { ColorPickerDialog(it) }!!

        setupCoordinators()

        team1ColorV.setOnClickListener {
            showColorDialog(1)
        }
        team2ColorV.setOnClickListener {
            showColorDialog(2)
        }
        mute.setOnClickListener {
            setIsMuted()
        }

        return fragmentView
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        Log.v(BottomSheetDialogFragment::class.qualifiedName, "onCancel triggered")

        runBlocking {
            setNames()
        }
    }

    private fun setupCoordinators() {
        context?.let {
            DataCoordinator.shared.initialize(
                context = it,
                onLoad = {
                    runBlocking {
                        getNamesFromData()
                        getColorFromData()
                        getIsMutedFromData()
                    }
                    updateTeamsNames()
                    updateTeamsColors()
                    updateIsMuted()
                }
            )
        }
    }

    private fun setNames() {
        DataCoordinator.shared.updateTeam1Name(team1NameV.text.toString())
        DataCoordinator.shared.updateTeam2Name(team2NameV.text.toString())
    }

    private fun setIsMuted() {
        DataCoordinator.shared.updateIsMuted(mute.isChecked)
    }

    private suspend fun getNamesFromData() {
        team1CurrentName = DataCoordinator.shared.getTeam1NameDataStore()
        team2CurrentName = DataCoordinator.shared.getTeam2NameDataStore()
    }

    private suspend fun getColorFromData() {
        team1CurrentColor = DataCoordinator.shared.getTeam1ColorDataStore()
        team2CurrentColor = DataCoordinator.shared.getTeam2ColorDataStore()
    }

    private suspend fun getIsMutedFromData() {
        isMuted = DataCoordinator.shared.getIsMuted()
    }

    private fun updateTeamsNames() {
        team1NameV.setText(team1CurrentName)
        team2NameV.setText(team2CurrentName)
    }

    private fun updateTeamsColors() {
        context?.let {
            team1ColorV.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    it,
                    team1CurrentColor
                )
            )
            team2ColorV.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    it,
                    team2CurrentColor
                )
            )
        }
    }

    private fun updateIsMuted() {
        mute.isChecked = isMuted
    }

    // TODO: does it need two loops?
    private fun showColorDialog(teamNumber: Int) {
        var icons = emptyArray<Drawable>()
        for (i in ColorOptions.colors.indices) {
            val circle = getCurrentDrawable(teamNumber, ColorOptions.colors[i])
            val fillColor = context?.let { ContextCompat.getColor(it, ColorOptions.colors[i]) }
            circle.colorFilter =
                fillColor?.let { PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
            icons += circle
        }

        dialog.teamNumber = teamNumber
        dialog.clearOptions()

        val colorNames = context?.let {
            ColorOptions.colorFromContext(it)
        }

        for (i in icons.indices) {
            dialog.addOption(icons[i], colorNames?.get(i) ?: "")
        }
        val alertDialog = dialog.create()
        alertDialog.setOnDismissListener {
            runBlocking {
                getColorFromData()
            }
            updateTeamsColors()
        }
        alertDialog.show()
    }

    private fun getCurrentDrawable(teamNumber: Int, currentColor: Int): Drawable {
        return context?.let {
            if (teamNumber == 1) {
                ContextCompat.getDrawable(
                    it,
                    if (currentColor == team1CurrentColor) R.drawable.circle_filled else R.drawable.circle
                )
            } else {
                ContextCompat.getDrawable(
                    it,
                    if (currentColor == team2CurrentColor) R.drawable.circle_filled else R.drawable.circle
                )
            }

        } as Drawable
    }


}