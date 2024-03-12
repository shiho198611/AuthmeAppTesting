package com.testing.githubusersdk.data.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicReference

class AsyncPagingDifferProvider<T : Any>(diffCallback: DiffUtil.ItemCallback<T>) {

    private val actualList: ArrayList<T> = ArrayList()

    private var pagingDataDiffer: AsyncPagingDataDiffer<T>
    private var testJob: Job? = null

    init {
        val differRef = AtomicReference<AsyncPagingDataDiffer<T>>(null)
        pagingDataDiffer = AsyncPagingDataDiffer(
            diffCallback, updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {
                    differRef.get()?.let { differ ->
                        val snapshot = differ.snapshot()
                        snapshot.forEach {
                            it?.apply {
                                actualList.add(this)
                            }
                        }
                        testJob?.cancel()
                    }
                }

                override fun onRemoved(position: Int, count: Int) = Unit

                override fun onMoved(fromPosition: Int, toPosition: Int) = Unit

                override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
            }
        )
        differRef.set(pagingDataDiffer)
    }

    fun getActualList(): ArrayList<T> {
        return actualList
    }

    suspend fun submitData(pagingData: PagingData<T>,testJob: Job?) {
        this.testJob = testJob
        pagingDataDiffer.submitData(pagingData)
    }

}