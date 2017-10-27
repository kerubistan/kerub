package com.github.kerubistan.kerub.utils.ipmi

enum class NetworkFunctionCode(val code: Int) {
	ChassisRequest(0x00),
	ChassisResponse(0x01),
	BridgeRequest(0x02),
	BridgeResponse(0x03),
	SensorEventRequest(0x04),
	SensorEventResponse(0x05),
	ApplicationRequest(0x06),
	ApplicationResponse(0x07),
	FirmwareRequest(0x08),
	FirmwareResponse(0x09),
	StorageRequest(0x0A),
	StorageResponse(0x0B),
	TransportRequest(0x0C),
	TransportResponse(0x0D)
}