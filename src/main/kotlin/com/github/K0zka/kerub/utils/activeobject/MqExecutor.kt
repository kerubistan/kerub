package com.github.K0zka.kerub.utils.activeobject

import javax.jms.MessageListener
import javax.jms.Message
import javax.jms.ObjectMessage


public class MqExecutor : Executor() , MessageListener {
	override fun onMessage(message: Message?) {
		execute( (message as ObjectMessage).getObject() as AsyncInvocation )
	}
}