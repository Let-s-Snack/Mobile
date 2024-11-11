package com.example.lets_snack.domain.mapper

import com.example.lets_snack.data.remote.dto.MessageDtoChat
import com.example.lets_snack.data.remote.dto.MessageUi

fun MessageDtoChat.mapToMessageUi( isLast: Boolean): MessageUi = MessageUi(message = message, username = username, isLast =  isLast)