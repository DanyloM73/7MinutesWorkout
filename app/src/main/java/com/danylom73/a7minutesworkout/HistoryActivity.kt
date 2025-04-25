package com.danylom73.a7minutesworkout

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.danylom73.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.historyToolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History"
        }

        binding.historyToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val dao = (application as WorkoutApp).db.historyDao()
        getAllCompletedDates(dao)
    }

    private fun getAllCompletedDates(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect { completedDates ->
                if (completedDates.isNotEmpty()) {
                    binding.historyTv.visibility = View.VISIBLE
                    binding.historyRv.visibility = View.VISIBLE
                    binding.noDataAvailableTv.visibility = View.INVISIBLE

                    binding.historyRv.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    val dates = ArrayList<String>()
                    for (date in completedDates) {
                       dates.add(date.date)
                    }

                    val historyAdapter = HistoryAdapter(dates)
                    binding.historyRv.adapter = historyAdapter
                } else {
                    binding.historyTv.visibility = View.GONE
                    binding.historyRv.visibility = View.GONE
                    binding.noDataAvailableTv.visibility = View.VISIBLE
                }
            }
        }
    }
}