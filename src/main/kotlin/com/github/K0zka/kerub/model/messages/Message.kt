package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="@type")
public trait Message : Serializable
