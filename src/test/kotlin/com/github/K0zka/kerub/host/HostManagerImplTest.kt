package com.github.K0zka.kerub.host

import org.junit.Test

public class HostManagerImplTest {
	Test fun getHostPubkey() {
		HostManagerImpl().getHostPublicKey("localhost")
	}
}