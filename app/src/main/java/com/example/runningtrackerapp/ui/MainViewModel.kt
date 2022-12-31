package com.example.runningtrackerapp.ui

import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningtrackerapp.db.entities.Run
import com.example.runningtrackerapp.repository.RunRepository
import com.example.runningtrackerapp.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
   private val repository: RunRepository
) : ViewModel() {


    private val runsSortedByDate = repository.getAllRunsByDate()

    private val runsSortedByCal = repository.getAllRunsByCal()

    private val runsSortedBySpeed = repository.getAllRunsBySpeed()

    private val runsSortedByDistance = repository.getAllRunsByDistance()

    private val runsSortedByTime = repository.getAllRunsByTime()


    fun insertRun(run : Run) {
            viewModelScope.launch {
                repository.insertRun(run)
            }
        }

    fun deleteRun(run : Run) = viewModelScope.launch {

        repository.deleteRun(run)
    }


    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.Date



    init {

        runs.addSource(runsSortedByDate){
            if(sortType == SortType.Date){
                it?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSortedByTime){
//            if(sortType == SortType.Time){
//                it?.let {
//                    runs.value = it
//                }
//            }
        }

        runs.addSource(runsSortedByDistance){
//            if(sortType == SortType.Distance){
//                it?.let {
//                    runs.value = it
//                }
//            }
        }

        runs.addSource(runsSortedBySpeed){
//            if(sortType == SortType.Speed){
//                it?.let {
//                    runs.value = it
//                }
//            }
        }

        runs.addSource(runsSortedByCal){
//            if(sortType == SortType.Calories){
//                it?.let {
//                    runs.value = it
//                }
//            }
        }


    }



    fun sortRuns(sortType: SortType){
        when(sortType){
            SortType.Date -> runsSortedByDate.value?.let {
                runs.value = it
            }
            SortType.Time -> runsSortedByTime.value?.let {
                runs.value = it
            }
            SortType.Distance -> runsSortedByDistance.value?.let {
                runs.value = it
            }
            SortType.Speed -> runsSortedBySpeed.value?.let {
                runs.value = it
            }
            SortType.Calories -> runsSortedByCal.value?.let {
                runs.value = it
            }
        }.also {

            this.sortType = sortType
        }
    }




}