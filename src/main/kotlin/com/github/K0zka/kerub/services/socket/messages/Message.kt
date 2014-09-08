package com.github.K0zka.kerub.services.socket.messages

import com.fasterxml.jackson.annotation.JsonTypeInfo

JsonTypeInfo(use=JsonTypeInfo.Id.NAME , include=JsonTypeInfo.As.PROPERTY, property="@type")
public trait Message {
}