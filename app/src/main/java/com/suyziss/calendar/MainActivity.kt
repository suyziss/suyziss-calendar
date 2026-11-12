package com.suyziss.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suyziss.calendar.data.AppDatabase
import com.suyziss.calendar.data.EventRepository
import com.suyziss.calendar.ui.screens.CalendarApp
import com.suyziss.calendar.ui.theme.SuyzissCalendarTheme
import com.suyziss.calendar.viewmodel.CalendarViewModel
import com.suyziss.calendar.viewmodel.CalendarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = EventRepository(database.eventDao())
        val viewModelFactory = CalendarViewModelFactory(repository)

        setContent {
            SuyzissCalendarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: CalendarViewModel = viewModel(factory = viewModelFactory)
                    CalendarApp(viewModel = viewModel)
                }
            }
        }
    }
}
