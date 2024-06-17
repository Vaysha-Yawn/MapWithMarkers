package com.appio.testapp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.appio.testapp.R
import com.appio.testapp.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    private val permissionActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            var r = false
            map.forEach { name, value ->
                if (name == Manifest.permission.ACCESS_FINE_LOCATION) {
                    r = value
                    invokeBannerNeeds()
                }
            }
            if (r) {
                next()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(layoutInflater)
        binding.continueDialog.visibility = View.GONE
        val view = binding.root
        val act = requireActivity() as MainActivity
        getPermissions(act, permissionActivityResultLauncher)
        return view
    }

    private fun getPermissions(
        context: Context,
        permissionActivityResultLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val arrPerm = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ).toTypedArray()

        if (!checkPermission(context, arrPerm)) {
            permissionActivityResultLauncher.launch(arrPerm)
        } else {
            next()
        }
    }

    private fun checkPermission(context: Context, array: Array<String>): Boolean {
        array.forEach {
            val value = ContextCompat.checkSelfPermission( context, it )
            if ( value != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun next() {
       findNavController().navigate(R.id.action_startFragment_to_mapFragment)
    }

    private fun invokeBannerNeeds() {
        binding.continueDialog.visibility = View.VISIBLE
        binding.apply.setOnClickListener {
            getPermissions(requireContext(), permissionActivityResultLauncher)
        }
        binding.exit.setOnClickListener {
            requireActivity().finish()
        }
    }

}