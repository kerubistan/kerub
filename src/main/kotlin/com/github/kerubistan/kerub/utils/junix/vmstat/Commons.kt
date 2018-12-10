package com.github.kerubistan.kerub.utils.junix.vmstat

import com.github.kerubistan.kerub.host.process
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

fun commonVmStat(session: ClientSession, interval: Int, out: OutputStream) = session.process("vmstat $interval", out)
