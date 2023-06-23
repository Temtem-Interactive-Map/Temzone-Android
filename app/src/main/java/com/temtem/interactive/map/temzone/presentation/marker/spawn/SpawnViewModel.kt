package com.temtem.interactive.map.temzone.presentation.marker.spawn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.presentation.marker.spawn.state.SpawnState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpawnViewModel @Inject constructor(
    private val temzoneRepository: TemzoneRepository,
) : ViewModel() {

    private val _spawnState: MutableStateFlow<SpawnState> = MutableStateFlow(SpawnState.Loading)
    val spawnState: SharedFlow<SpawnState> = _spawnState.asSharedFlow()

    fun getSpawn(id: String) {
        viewModelScope.launch {
            try {
                val spawn = temzoneRepository.getSpawn(id)

                _spawnState.update {
                    SpawnState.Success(spawn)
                }
            } catch (exception: Exception) {
                _spawnState.update {
                    SpawnState.Error(exception.message.orEmpty())
                }
            }
        }
    }
}
