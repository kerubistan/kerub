package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.messages.Message

public trait EventListener {
    fun send(message : Message)
}