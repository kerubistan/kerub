package com.github.K0zka.kerub.services.socket.messages

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonCreator


JsonTypeName("ping")
JsonCreator
public class PingMessage : Message {
}