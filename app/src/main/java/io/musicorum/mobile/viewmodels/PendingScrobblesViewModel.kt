package io.musicorum.mobile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.models.PendingScrobble
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PendingScrobblesViewModel(application: Application) : AndroidViewModel(application) {
    val lastSyncStatus = MutableLiveData("")
    val pendingScrobbles = MutableLiveData<List<PendingScrobble>>(emptyList())

    init {
        val work = WorkManager.getInstance(application)
            .getWorkInfosByTag("SYNC_SCROBBLES")
            .get()
        lastSyncStatus.value = work.getOrNull(0)?.state?.name
        Log.d("PendingScrobbles", "Worker will run at ${work.getOrNull(0)?.nextScheduleTimeMillis}")

        val dao = PendingScrobblesDb.getDatabase(application).pendingScrobblesDao()
        val repo = PendingScrobblesRepository(dao)
        viewModelScope.launch {
            val list = repo.getAllScrobblesStream().first()
            pendingScrobbles.value = list
        }
    }
}