package com.example.runningtrackerapp.ui.fragments


import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.adapters.RunAdapter
import com.example.runningtrackerapp.databinding.FragmentRunBinding
import com.example.runningtrackerapp.ui.MainActivity
import com.example.runningtrackerapp.ui.MainViewModel
import com.example.runningtrackerapp.utils.SortType
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class RunFragment : BaseFragment<FragmentRunBinding>(
    FragmentRunBinding::inflate
) {

    private val viewModel : MainViewModel by viewModels()

    private lateinit var runAdapter : RunAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.fab.setOnClickListener{

            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)

        }

        (activity as MainActivity).requestPermissions(requireContext())
        if(Build.VERSION.SDK_INT >= Q){
            (activity as MainActivity).requestBackGroundPermission(requireContext())
        }



        setUpRecycleView()
//
//        when(viewModel.sortType){
//            SortType.Date -> bind.spFilter.setSelection(0)
//            SortType.Time -> bind.spFilter.setSelection(1)
//            SortType.Distance -> bind.spFilter.setSelection(2)
//            SortType.Speed -> bind.spFilter.setSelection(3)
//            SortType.Calories -> bind.spFilter.setSelection(4)
//
//        }

        bind.spFilter.onItemSelectedListener = object:  AdapterView.OnItemSelectedListener{

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.sortRuns(SortType.Date)
                    1 -> viewModel.sortRuns(SortType.Time)
                    2 -> viewModel.sortRuns(SortType.Distance)
                    3 -> viewModel.sortRuns(SortType.Speed)
                    4 -> viewModel.sortRuns(SortType.Calories)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        viewModel.runs.observe(viewLifecycleOwner){

            runAdapter.submitList(it)

        }

        val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val run = runAdapter.differ.currentList[position]
                viewModel.deleteRun(run).also {
                    Snackbar.make(view, "Run deleted", Snackbar.LENGTH_LONG).apply {

                        setAction("UNDO") {
                            viewModel.insertRun(run)
                        }
                    }.show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(bind.rvRuns)
        }
    }





   private fun setUpRecycleView(){

        runAdapter = RunAdapter()

        bind.rvRuns.apply {
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }



}