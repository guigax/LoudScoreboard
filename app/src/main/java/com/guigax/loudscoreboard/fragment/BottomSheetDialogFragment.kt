package com.guigax.loudscoreboard.fragment

import ImageAdapter
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guigax.loudscoreboard.R
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.getTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam2NameDataStore
import kotlinx.coroutines.runBlocking


class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var team1ColorV: Button
    private lateinit var team2ColorV: Button
    private lateinit var team1NameV: EditText
    private lateinit var team2NameV: EditText

    private var team1CurrentName = ""
    private var team2CurrentName = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        setupCoordinators()

        val spinner: Spinner = fragmentView.findViewById(R.id.sound)
        val images = arrayOf(R.drawable.whistle, R.drawable.ding, R.drawable.horn)
        val adapter = ImageAdapter(fragmentView.context, images)
        spinner.adapter = adapter

        team1ColorV = fragmentView.findViewById(R.id.team1Color)
        team2ColorV = fragmentView.findViewById(R.id.team2Color)
        team1NameV = fragmentView.findViewById(R.id.team1Name)
        team2NameV = fragmentView.findViewById(R.id.team2Name)

        team1ColorV.setOnClickListener {
            Toast.makeText(
                fragmentView.context,
                "Not yet",
                Toast.LENGTH_SHORT
            ).show()
        }
        team2ColorV.setOnClickListener {
            Toast.makeText(
                fragmentView.context,
                "Not yet",
                Toast.LENGTH_SHORT
            ).show()
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
                    }
                    updateTeamsNames()
                }
            )
        }
    }

    private suspend fun setNames() {
        DataCoordinator.shared.setTeam1NameDataStore(team1NameV.text.toString())
        DataCoordinator.shared.setTeam2NameDataStore(team2NameV.text.toString())
    }

    private suspend fun getNamesFromData() {
        team1CurrentName = DataCoordinator.shared.getTeam1NameDataStore()
        team2CurrentName = DataCoordinator.shared.getTeam2NameDataStore()
    }

    private fun updateTeamsNames() {
        team1NameV.setText(team1CurrentName)
        team2NameV.setText(team2CurrentName)
    }
}