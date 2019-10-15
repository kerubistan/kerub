package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.testDisk
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever

fun <T : Asset> AssetAccessController.mockAccessGranted(asset: T) {
	doAnswer { mockInvocation ->
		(mockInvocation.arguments[1] as () -> Any).invoke()
	}.whenever(this).checkAndDo(eq(testDisk), any<() -> Any>())
	doAnswer { mockInvocation ->
		(mockInvocation.arguments[0] as () -> T).invoke()
	}.whenever(this).doAndCheck(any<() -> T>())
}