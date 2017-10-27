package com.github.kerubistan.kerub.utils.ipmi

enum class CompletionCodes(val code: Int) {
	CompletedNormally(0x00),
	NodeBusy(0xC0),
	InvalidCommand(0xC1),
	CommandInvalidForLun(0xC2),
	Timeout(0xC3),
	OutOfSpace(0xC4),
	ReservationCanceled(0xC5),
	RequestDataTruncated(0xC6),
	RequestDataLengthInvalid(0xC7),
	RequestDataFieldLengthLimitExceeded(0xC8),
	ParameterOutOfRange(0xC9)
}