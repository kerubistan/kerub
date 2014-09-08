package com.github.K0zka.kerub.services.socket.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("unsubscribe")
JsonCreator
public class UnsubscribeMessage ( val channel : String) : Message {
}