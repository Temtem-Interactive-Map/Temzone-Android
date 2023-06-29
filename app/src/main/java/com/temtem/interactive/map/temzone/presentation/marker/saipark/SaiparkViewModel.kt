package com.temtem.interactive.map.temzone.presentation.marker.saipark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.presentation.marker.saipark.state.SaiparkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaiparkViewModel @Inject constructor(
    private val temzoneRepository: TemzoneRepository,
) : ViewModel() {

    private val _saiparkState: MutableStateFlow<SaiparkState> =
        MutableStateFlow(SaiparkState.Loading)
    val saiparkState: SharedFlow<SaiparkState> = _saiparkState.asSharedFlow()

    fun getSaipark(id: String) {
        _saiparkState.update {
            SaiparkState.Loading
        }

        viewModelScope.launch {
            try {
                val saipark = temzoneRepository.getSaipark(id)

                _saiparkState.update {
                    SaiparkState.Success(saipark)
                }
            } catch (exception: Exception) {
                _saiparkState.update {
                    SaiparkState.Error(exception.message.orEmpty())
                }
            }
        }
    }
}
