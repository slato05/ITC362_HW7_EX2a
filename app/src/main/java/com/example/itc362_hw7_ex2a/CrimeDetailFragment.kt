package com.example.itc362_hw7_ex2a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.itc362_hw7_ex2a.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.util.*

//private const val TAG = "CrimeDetailFragment"

class CrimeDetailFragment :Fragment(){

    // private lateinit var crime:Crime
    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    //private lateinit var binding :FragmentCrimeDetailBinding
    private var _binding : FragmentCrimeDetailBinding? =  null

    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null. Is the view visisble"

        }

    /*  override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)

          crime = Crime(

              UUID.randomUUID(),
              title = "",
              date = Date(),
              isSolved = false

          )
          Log.d(TAG, "The crime ID is: ${args.crimeId}")
      }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    //Wiring up views in a fragment



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            // listener for edit text
            crimeTitle.doOnTextChanged{text,_,_,_ ->
                // crime = crime.copy(title = text.toString())`false`
                crimeDetailViewModel.updateCrime { oldCrime -> oldCrime.copy(title = text.toString())
                }

            }

            // listener for button
            crimeDate.apply {
                //  text = crime.date.toString()
                isEnabled = false

            }

            // listener for textbox changes
            crimeSolved.setOnCheckedChangeListener{_, isCheckeed ->
                //  crime = crime.copy(isSolved = isCheckeed)
                crimeDetailViewModel.updateCrime { oldCrime -> oldCrime.copy(isSolved = isCheckeed)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        // If the title of crime is blank after update, require user enter a title
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val title = binding.crimeTitle.text.toString()
                if (title.isBlank()) {
                    Toast.makeText(context, "Please provide a description of the crime.", Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun updateUi(crime:Crime) {

        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeSolved.isChecked = crime.isSolved
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
        _binding = null
    }

}