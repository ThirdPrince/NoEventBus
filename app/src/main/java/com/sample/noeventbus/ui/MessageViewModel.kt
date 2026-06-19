package com.sample.noeventbus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    
    fun addMessage() {
        viewModelScope.launch {
            messageRepository.addMessage()
        }
    }
    
    fun clearMessages() {
        viewModelScope.launch {
            messageRepository.clearMessages()
        }
    }
}
