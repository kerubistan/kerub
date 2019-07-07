package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.testDisk
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever

fun AssetAccessController.mockAccessGranted(asset : Asset) {
	doAnswer {mockInvocation ->
		(mockInvocation.arguments[1] as () -> Any).invoke()
	}.whenever(this).checkAndDo(eq(testDisk), any<() -> Any>())
}