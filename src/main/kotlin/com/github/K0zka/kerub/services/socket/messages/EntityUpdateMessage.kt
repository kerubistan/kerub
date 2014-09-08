package com.github.K0zka.kerub.services.socket.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("update")
JsonCreator
public class EntityUpdateMessage (val obj : Any, val date : Long) : Message {

}