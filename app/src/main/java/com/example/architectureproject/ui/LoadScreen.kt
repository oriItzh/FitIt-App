package com.example.architectureproject.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.architectureproject.R
import com.example.architectureproject.databinding.LoadScreenBinding
import com.example.architectureproject.databinding.ProfileFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TIME = 3300L

class LoadScreenFragment : Fragment() {

    private var _binding: LoadScreenBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = LoadScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The widgets on the screen
        val logo = binding.letters
        val lift = binding.lift

        // Creates Animation for fade in of the logo
        val fadeInAnimatorLetters: Animator =
            ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f).setDuration(700)

        val fadeInAnimatorLift: Animator =
            ObjectAnimator.ofFloat(lift, "alpha", 0f, 1f).setDuration(200)

        val translateLift: Animator = ObjectAnimator.ofFloat(lift, "translationY", 0f, 850f).apply {
            duration = 700
            interpolator = AccelerateInterpolator()  // Default factor is 1.0 which can be changed
        }

        val scaleBig = AnimatorSet().apply {
            val scaleLiftX = ObjectAnimator.ofFloat(lift, "scaleX", 1f, 1.3f).apply {
                duration = 1000
            }
            val scaleLiftY = ObjectAnimator.ofFloat(lift, "scaleY", 1f, 1.3f).apply {
                duration = 1000
            }
            val scaleLettersX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 1.3f).apply {
                duration = 1000
            }
            val scaleLettersY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 1.3f).apply {
                duration = 1000
            }
            playTogether(scaleLettersX, scaleLettersY, scaleLiftX, scaleLiftY)
        }

        val scaleSmall = AnimatorSet().apply {
            val scaleLiftX = ObjectAnimator.ofFloat(lift, "scaleX", 1.3f, 1f).apply {
                duration = 1000
            }
            val scaleLiftY = ObjectAnimator.ofFloat(lift, "scaleY", 1.3f, 1f).apply {
                duration = 1000
            }
            val scaleLettersX = ObjectAnimator.ofFloat(logo, "scaleX", 1.3f, 1f).apply {
                duration = 1000
            }
            val scaleLettersY = ObjectAnimator.ofFloat(logo, "scaleY", 1.3f, 1f).apply {
                duration = 1000
            }
            playTogether(scaleLettersX, scaleLettersY, scaleLiftX, scaleLiftY)
        }

        // Creates an AnimatorSet for playing animations together
        val togetherAnimatorSet = AnimatorSet().apply {
            playTogether(fadeInAnimatorLift, translateLift)
        }

        // Plays the animations sequentially
        val animatorSet = AnimatorSet().apply {
            playSequentially(fadeInAnimatorLetters, togetherAnimatorSet, scaleBig, scaleSmall)
        }

        // Starts the animation set
        animatorSet.start()

        // Simulate loading time or initialization
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_loadScreenFragment_to_profileFragment)
        }, TIME)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}