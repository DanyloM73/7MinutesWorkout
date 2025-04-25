package com.danylom73.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.danylom73.a7minutesworkout.databinding.ActivityExerciseBinding
import java.util.Locale
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.danylom73.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var restTimer: CountDownTimer
    private lateinit var exerciseTimer: CountDownTimer
    private lateinit var exerciseList: ArrayList<ExerciseModel>
    private lateinit var tts: TextToSpeech
    private lateinit var player: MediaPlayer
    private lateinit var exerciseStatusAdapter: ExerciseStatusAdapter

    private var progressValue = 0
    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        try {
            val soundUri =
                ("android.resource://com.danylom73.a7minutesworkout/"
                        + R.raw.press_start).toUri()
            player = MediaPlayer.create(applicationContext, soundUri)
            player.isLooping = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setSupportActionBar(binding.exerciseToolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.exerciseToolbar.setNavigationOnClickListener {
            //onBackPressedDispatcher.onBackPressed()
            showBackDialog()
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        setRestProgressBar()
        setupExerciseStatusRecyclerView()
    }

    private fun setupExerciseStatusRecyclerView() {
        binding.exerciseStatusRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList)
        binding.exerciseStatusRv.adapter = exerciseStatusAdapter
    }

    private fun setRestProgressBar() {
        if (currentExercisePosition == -1) {
            binding.upcomingExerciseTv.text = getString(R.string.upcoming_exercise,
                exerciseList[currentExercisePosition + 1].name)
        }
        progressValue = 0
        binding.progressBar.progress = progressValue
        binding.titleTv.text = getString(R.string.get_ready_text)
        binding.upcomingExerciseTv.visibility = View.VISIBLE
        binding.progressBar.max = 10
        restTimer = createTimer(10) {
            startExercise()
        }.start()
    }

    private fun startExercise() {
        progressValue = 0
        currentExercisePosition++
        exerciseList[currentExercisePosition].isSelected = true
        exerciseStatusAdapter.notifyDataSetChanged()
        binding.exerciseIv.visibility = View.VISIBLE
        binding.upcomingExerciseTv.visibility = View.INVISIBLE
        binding.exerciseIv.setImageResource(exerciseList[currentExercisePosition].image)
        binding.titleTv.text = exerciseList[currentExercisePosition].name
        binding.progressBar.max = 30
        binding.progressBar.progress = progressValue
        exerciseTimer = createTimer(30) {
            player.start()
            exerciseList[currentExercisePosition].isCompleted = true
            exerciseList[currentExercisePosition].isSelected = false
            exerciseStatusAdapter.notifyDataSetChanged()
            binding.exerciseIv.visibility = View.INVISIBLE
            if (currentExercisePosition < exerciseList.size - 1) {
                binding.upcomingExerciseTv.text = getString(R.string.upcoming_exercise,
                    exerciseList[currentExercisePosition + 1].name)
                setRestProgressBar()
            } else {
                finish()
                startActivity(Intent(this, FinishActivity::class.java))
            }
        }.start()
        speakOut(exerciseList[currentExercisePosition].name)
    }

    private fun createTimer(
        time: Int,
        interval: Int = 1,
        onFinish: () -> Unit
    ): CountDownTimer {
        return object : CountDownTimer((time * 100).toLong(), (interval * 100).toLong()) {
            override fun onTick(p0: Long) {
                progressValue++
                binding.progressBar.progress = time - progressValue
                binding.timerTv.text = (time - progressValue).toString()
            }

            override fun onFinish() {
                onFinish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        restTimer.cancel()
        exerciseTimer.cancel()
        progressValue = 0
        tts.stop()
        tts.shutdown()
        player.stop()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Specified language is not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "")
    }

    private fun showBackDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.yesBtn.setOnClickListener {
            this@ExerciseActivity.finish()
            dialog.dismiss()
        }
        dialogBinding.noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
