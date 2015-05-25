package com.github.K0zka.kerub.utils.junix.dmi

val mylaptop = """
# dmidecode 2.12
SMBIOS 2.7 present.
51 structures occupying 2201 bytes.
Table at 0x000E4800.

Handle 0x0000, DMI type 0, 24 bytes
BIOS Information
	Vendor: LENOVO
	Version: 6CCN18WW(V1.05)
	Release Date: 07/18/2012
	Address: 0xE0000
	Runtime Size: 128 kB
	ROM Size: 4096 kB
	Characteristics:
		PCI is supported
		BIOS is upgradeable
		BIOS shadowing is allowed
		Boot from CD is supported
		Selectable boot is supported
		BIOS ROM is socketed
		EDD is supported
		Japanese floppy for NEC 9800 1.2 MB is supported (int 13h)
		Japanese floppy for Toshiba 1.2 MB is supported (int 13h)
		5.25"/360 kB floppy services are supported (int 13h)
		5.25"/1.2 MB floppy services are supported (int 13h)
		3.5"/720 kB floppy services are supported (int 13h)
		3.5"/2.88 MB floppy services are supported (int 13h)
		8042 keyboard services are supported (int 9h)
		CGA/mono video services are supported (int 10h)
		ACPI is supported
		USB legacy is supported
		Targeted content distribution is supported
		UEFI is supported
	BIOS Revision: 0.18
	Firmware Revision: 0.0

Handle 0x0001, DMI type 1, 27 bytes
System Information
	Manufacturer: LENOVO
	Product Name: 20137
	Version: Lenovo G585
	Serial Number: 2997455000045
	UUID: E0E4DD45-D827-E211-840D-B888E398C20C
	Wake-up Type: Power Switch
	SKU Number: 123456789
	Family: IDEAPAD

Handle 0x0002, DMI type 2, 16 bytes
Base Board Information
	Manufacturer: LENOVO
	Product Name: Lenovo G585
	Version: INVALID
	Serial Number: CB19676990
	Asset Tag: No Asset Tag
	Features:
		Board is a hosting board
		Board is replaceable
	Location In Chassis: Base Board Chassis Location
	Chassis Handle: 0x0003
	Type: Motherboard
	Contained Object Handles: 0

Handle 0x0003, DMI type 3, 23 bytes
Chassis Information
	Manufacturer: LENOVO
	Type: Notebook
	Lock: Not Present
	Version: Lenovo G585
	Serial Number: CB19676990
	Asset Tag: No Asset Tag
	Boot-up State: Safe
	Power Supply State: Safe
	Thermal State: Safe
	Security Status: None
	OEM Information: 0x00000000
	Height: Unspecified
	Number Of Power Cords: 1
	Contained Elements: 0
	SKU Number: Not Specified

Handle 0x0004, DMI type 4, 42 bytes
Processor Information
	Socket Designation: Socket FT1
	Type: Central Processor
	Family: E-Series
	Manufacturer: AMD processor
	ID: 20 0F 50 00 FF FB 8B 17
	Signature: Family 20, Model 2, Stepping 0
	Flags:
		FPU (Floating-point unit on-chip)
		VME (Virtual mode extension)
		DE (Debugging extension)
		PSE (Page size extension)
		TSC (Time stamp counter)
		MSR (Model specific registers)
		PAE (Physical address extension)
		MCE (Machine check exception)
		CX8 (CMPXCHG8 instruction supported)
		APIC (On-chip APIC hardware supported)
		SEP (Fast system call)
		MTRR (Memory type range registers)
		PGE (Page global enable)
		MCA (Machine check architecture)
		CMOV (Conditional move instruction supported)
		PAT (Page attribute table)
		PSE-36 (36-bit page size extension)
		CLFSH (CLFLUSH instruction supported)
		MMX (MMX technology supported)
		FXSR (FXSAVE and FXSTOR instructions supported)
		SSE (Streaming SIMD extensions)
		SSE2 (Streaming SIMD extensions 2)
		HTT (Multi-threading)
	Version: AMD E2-1800 APU with Radeon(tm) HD Graphics
	Voltage: 1.4 V
	External Clock: 100 MHz
	Max Speed: 1700 MHz
	Current Speed: 1700 MHz
	Status: Populated, Enabled
	Upgrade: None
	L1 Cache Handle: 0x0008
	L2 Cache Handle: 0x0009
	L3 Cache Handle: Not Provided
	Serial Number: NotSupport
	Asset Tag: FFFF
	Part Number: FFFF
	Core Count: 2
	Core Enabled: 2
	Thread Count: 2
	Characteristics:
		64-bit capable

Handle 0x0005, DMI type 5, 20 bytes
Memory Controller Information
	Error Detecting Method: None
	Error Correcting Capabilities:
		None
	Supported Interleave: One-way Interleave
	Current Interleave: One-way Interleave
	Maximum Memory Module Size: 8192 MB
	Maximum Total Memory Size: 16384 MB
	Supported Speeds:
		Other
		Unknown
		70 ns
		60 ns
		50 ns
	Supported Memory Types:
		Other
		Unknown
		Standard
		FPM
		SIMM
		Burst EDO
	Memory Module Voltage: 3.3 V
	Associated Memory Slots: 2
		0x0006
		0x0007
	Enabled Error Correcting Capabilities:
		None
		Single-bit Error Correcting
		Double-bit Error Correcting
		Error Scrubbing

Handle 0x0006, DMI type 6, 12 bytes
Memory Module Information
	Socket Designation: DIMM 0
	Bank Connections: None
	Current Speed: 1 ns
	Type: DIMM
	Installed Size: 4096 MB (Single-bank Connection)
	Enabled Size: 4096 MB (Single-bank Connection)
	Error Status: OK

Handle 0x0007, DMI type 6, 12 bytes
Memory Module Information
	Socket Designation: DIMM 1
	Bank Connections: None
	Current Speed: 1 ns
	Type: DIMM
	Installed Size: 4096 MB (Single-bank Connection)
	Enabled Size: 4096 MB (Single-bank Connection)
	Error Status: OK

Handle 0x0008, DMI type 7, 19 bytes
Cache Information
	Socket Designation: L1 Cache
	Configuration: Enabled, Not Socketed, Level 1
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 128 kB
	Maximum Size: 128 kB
	Supported SRAM Types:
		Pipeline Burst
	Installed SRAM Type: Pipeline Burst
	Speed: 1 ns
	Error Correction Type: Multi-bit ECC
	System Type: Unified
	Associativity: 2-way Set-associative

Handle 0x0009, DMI type 7, 19 bytes
Cache Information
	Socket Designation: L2 Cache
	Configuration: Enabled, Not Socketed, Level 2
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 1024 kB
	Maximum Size: 1024 kB
	Supported SRAM Types:
		Pipeline Burst
	Installed SRAM Type: Pipeline Burst
	Speed: 1 ns
	Error Correction Type: Multi-bit ECC
	System Type: Unified
	Associativity: 16-way Set-associative

Handle 0x000A, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1A1
	Internal Connector Type: None
	External Reference Designator: Keyboard
	External Connector Type: PS/2
	Port Type: Keyboard Port

Handle 0x000B, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1A1
	Internal Connector Type: None
	External Reference Designator: Mouse
	External Connector Type: PS/2
	Port Type: Mouse Port

Handle 0x000C, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A2
	Internal Connector Type: None
	External Reference Designator: COM 1
	External Connector Type: DB-9 male
	Port Type: Serial Port 16550A Compatible

Handle 0x000D, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000E, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000F, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0010, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J5A1
	Internal Connector Type: None
	External Reference Designator: USB
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0011, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J5A1
	Internal Connector Type: None
	External Reference Designator: USB
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0012, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J5A1
	Internal Connector Type: None
	External Reference Designator: Network
	External Connector Type: RJ-45
	Port Type: Network Port

Handle 0x0013, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9G2
	Internal Connector Type: On Board Floppy
	External Reference Designator: OnBoard Floppy Type
	External Connector Type: None
	Port Type: Other

Handle 0x0014, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J7J1
	Internal Connector Type: On Board IDE
	External Reference Designator: OnBoard Primary IDE
	External Connector Type: None
	Port Type: Other

Handle 0x0015, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A1
	Internal Connector Type: None
	External Reference Designator: TV OUT
	External Connector Type: Mini DIN
	Port Type: Video Port

Handle 0x0016, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A2
	Internal Connector Type: None
	External Reference Designator: CRT
	External Connector Type: DB-15 female
	Port Type: Video Port

Handle 0x0017, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J30
	Internal Connector Type: None
	External Reference Designator: Microphone In
	External Connector Type: Mini Jack (headphones)
	Port Type: Audio Port

Handle 0x0018, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J30
	Internal Connector Type: None
	External Reference Designator: Line In
	External Connector Type: Mini Jack (headphones)
	Port Type: Audio Port

Handle 0x0019, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J30
	Internal Connector Type: None
	External Reference Designator: Speaker Out
	External Connector Type: Mini Jack (headphones)
	Port Type: Audio Port

Handle 0x001A, DMI type 9, 17 bytes
System Slot Information
	Designation: J6C1
	Type: x16 PCI Express x16
	Current Usage: Available
	Length: Long
	ID: 1
	Characteristics:
		3.3 V is provided
		PME signal is supported
		Hot-plug devices are supported
	Bus Address: 0000:00:02.0

Handle 0x001B, DMI type 9, 17 bytes
System Slot Information
	Designation: J8C1
	Type: x1 PCI Express x1
	Current Usage: Available
	Length: Short
	ID: 2
	Characteristics:
		3.3 V is provided
		PME signal is supported
		Hot-plug devices are supported
	Bus Address: 0000:00:04.0

Handle 0x001C, DMI type 9, 17 bytes
System Slot Information
	Designation: J7C1
	Type: x1 PCI Express x1
	Current Usage: Available
	Length: Short
	ID: 3
	Characteristics:
		3.3 V is provided
		PME signal is supported
		Hot-plug devices are supported
	Bus Address: 0000:00:05.0

Handle 0x001D, DMI type 9, 17 bytes
System Slot Information
	Designation: J8D1
	Type: x1 PCI Express x1
	Current Usage: Available
	Length: Short
	ID: 4
	Characteristics:
		3.3 V is provided
		PME signal is supported
		Hot-plug devices are supported
	Bus Address: 0000:00:06.0

Handle 0x001E, DMI type 9, 17 bytes
System Slot Information
	Designation: J8B1
	Type: 32-bit PCI
	Current Usage: Available
	Length: Long
	ID: 5
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		Cardbus is supported
		Modem ring resume is supported
		PME signal is supported
		SMBus signal is supported
	Bus Address: 0000:00:14.4

Handle 0x001F, DMI type 11, 5 bytes
OEM Strings
	String 1: String1 for Original Equipment Manufacturer
	String 2: String2 for Original Equipment Manufacturer
	String 3: String3 for Original Equipment Manufacturer
	String 4: String4 for Original Equipment Manufacturer
	String 5: String5 for Original Equipment Manufacturer

Handle 0x0020, DMI type 12, 5 bytes
System Configuration Options
	Option 1: String1 for Type12 Equipment Manufacturer
	Option 2: String2 for Type12 Equipment Manufacturer
	Option 3: String3 for Type12 Equipment Manufacturer
	Option 4: String4 for Type12 Equipment Manufacturer

Handle 0x0021, DMI type 13, 22 bytes
BIOS Language Information
	Language Description Format: Long
	Installable Languages: 4
		en|US|iso8859-1
		fr|CA|iso8859-1
		ja|JP|unicode
		zh|TW|unicode
	Currently Installed Language: en|US|iso8859-1

Handle 0x0022, DMI type 16, 23 bytes
Physical Memory Array
	Location: System Board Or Motherboard
	Use: System Memory
	Error Correction Type: None
	Maximum Capacity: 8 GB
	Error Information Handle: 0x0027
	Number Of Devices: 2

Handle 0x0023, DMI type 17, 34 bytes
Memory Device
	Array Handle: 0x0022
	Error Information Handle: 0x0025
	Total Width: 64 bits
	Data Width: 64 bits
	Size: 4096 MB
	Form Factor: SODIMM
	Set: None
	Locator: DIMM 0
	Bank Locator: CHANNEL A
	Type: DDR3
	Type Detail: Synchronous Unbuffered (Unregistered)
	Speed: 800 MHz
	Manufacturer: Not Specified
	Serial Number: 413A0382
	Asset Tag: Asset Tag:
	Part Number: RMT3160ED58E9W1600
	Rank: 2
	Configured Clock Speed: 667 MHz

Handle 0x0024, DMI type 17, 34 bytes
Memory Device
	Array Handle: 0x0022
	Error Information Handle: 0x0026
	Total Width: 64 bits
	Data Width: 64 bits
	Size: 4096 MB
	Form Factor: SODIMM
	Set: None
	Locator: DIMM 1
	Bank Locator: CHANNEL A
	Type: DDR3
	Type Detail: Synchronous Unbuffered (Unregistered)
	Speed: 800 MHz
	Manufacturer: Not Specified
	Serial Number: 41000382
	Asset Tag: Asset Tag:
	Part Number: RMT3160ED58E9W1600
	Rank: 2
	Configured Clock Speed: 667 MHz

Handle 0x0025, DMI type 18, 23 bytes
32-bit Memory Error Information
	Type: OK
	Granularity: Unknown
	Operation: Unknown
	Vendor Syndrome: Unknown
	Memory Array Address: Unknown
	Device Address: Unknown
	Resolution: Unknown

Handle 0x0026, DMI type 18, 23 bytes
32-bit Memory Error Information
	Type: OK
	Granularity: Unknown
	Operation: Unknown
	Vendor Syndrome: Unknown
	Memory Array Address: Unknown
	Device Address: Unknown
	Resolution: Unknown

Handle 0x0027, DMI type 18, 23 bytes
32-bit Memory Error Information
	Type: OK
	Granularity: Unknown
	Operation: Unknown
	Vendor Syndrome: Unknown
	Memory Array Address: Unknown
	Device Address: Unknown
	Resolution: Unknown

Handle 0x0028, DMI type 19, 31 bytes
Memory Array Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x001FFFFFFFF
	Range Size: 8 GB
	Physical Array Handle: 0x0022
	Partition Width: 255

Handle 0x0029, DMI type 20, 35 bytes
Memory Device Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x001FFFFFFFF
	Range Size: 8 GB
	Physical Device Handle: 0x0023
	Memory Array Mapped Address Handle: 0x0028
	Partition Row Position: Unknown
	Interleave Position: Unknown
	Interleaved Data Depth: Unknown

Handle 0x002A, DMI type 20, 35 bytes
Memory Device Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x001FFFFFFFF
	Range Size: 8 GB
	Physical Device Handle: 0x0024
	Memory Array Mapped Address Handle: 0x0028
	Partition Row Position: Unknown
	Interleave Position: Unknown
	Interleaved Data Depth: Unknown

Handle 0x002B, DMI type 21, 7 bytes
Built-in Pointing Device
	Type: Touch Pad
	Interface: PS/2
	Buttons: 4

Handle 0x002C, DMI type 26, 22 bytes
Voltage Probe
	Description: Voltage Probe Description.
	Location: Unknown
	Status: Unknown
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x002D, DMI type 32, 20 bytes
System Boot Information
	Status: No errors detected

Handle 0x002E, DMI type 40, 18 bytes
Additional Information 1
	Referenced Handle: 0x001a
	Referenced Offset: 0x05
	String: PCIExpressx16
	Value: 0xaa
Additional Information 2
	Referenced Handle: 0x0000
	Referenced Offset: 0x05
	String: Compiler Version: VC 9.0
	Value: 0x05dc

Handle 0x002F, DMI type 41, 11 bytes
Onboard Device
	Reference Designation: 82567LM Gigabit Network Connection
	Type: Ethernet
	Status: Enabled
	Type Instance: 1
	Bus Address: 0000:00:00.1

Handle 0x0030, DMI type 128, 8 bytes
OEM-specific Type
	Header and Data:
		80 08 30 00 55 AA 55 AA
	Strings:
		Oem Test 1
		Oem Test 2

Handle 0x0031, DMI type 133, 5 bytes
OEM-specific Type
	Header and Data:
		85 05 31 00 01
	Strings:
		KHOIHGIUCCHHII

Handle 0x0032, DMI type 127, 4 bytes
End Of Table
"""

val nuc = """
# dmidecode 2.12
SMBIOS 2.8 present.
84 structures occupying 3174 bytes.
Table at 0x000EC170.

Handle 0x0000, DMI type 0, 24 bytes
BIOS Information
	Vendor: Intel Corp.
	Version: WYLPT10H.86A.0026.2014.0514.1714
	Release Date: 05/14/2014
	Address: 0xF0000
	Runtime Size: 64 kB
	ROM Size: 6656 kB
	Characteristics:
		PCI is supported
		BIOS is upgradeable
		BIOS shadowing is allowed
		Boot from CD is supported
		Selectable boot is supported
		BIOS ROM is socketed
		EDD is supported
		5.25"/1.2 MB floppy services are supported (int 13h)
		3.5"/720 kB floppy services are supported (int 13h)
		3.5"/2.88 MB floppy services are supported (int 13h)
		Print screen service is supported (int 5h)
		Serial services are supported (int 14h)
		Printer services are supported (int 17h)
		ACPI is supported
		USB legacy is supported
		BIOS boot specification is supported
		Targeted content distribution is supported
		UEFI is supported
	BIOS Revision: 4.6

Handle 0x0001, DMI type 1, 27 bytes
System Information
	Manufacturer:
	Product Name:
	Version:
	Serial Number:
	UUID: 3DACA680-34DC-11E1-988E-C03FD56F97FC
	Wake-up Type: Power Switch
	SKU Number:
	Family:

Handle 0x0002, DMI type 2, 15 bytes
Base Board Information
	Manufacturer: Intel Corporation
	Product Name: D34010WYK
	Version: H14771-304
	Serial Number: GEWY442005V7
	Asset Tag:
	Features:
		Board is a hosting board
		Board is replaceable
	Location In Chassis: To be filled by O.E.M.
	Chassis Handle: 0x0003
	Type: Motherboard
	Contained Object Handles: 0

Handle 0x0003, DMI type 3, 25 bytes
Chassis Information
	Manufacturer:
	Type: Desktop
	Lock: Not Present
	Version:
	Serial Number:
	Asset Tag:
	Boot-up State: Safe
	Power Supply State: Safe
	Thermal State: Safe
	Security Status: None
	OEM Information: 0x00000000
	Height: Unspecified
	Number Of Power Cords: 1
	Contained Elements: 1
		<OUT OF SPEC> (0)
	SKU Number: To be filled by O.E.M.

Handle 0x0004, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1A1
	Internal Connector Type: None
	External Reference Designator: PS2Mouse
	External Connector Type: PS/2
	Port Type: Mouse Port

Handle 0x0005, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1A1
	Internal Connector Type: None
	External Reference Designator: Keyboard
	External Connector Type: PS/2
	Port Type: Keyboard Port

Handle 0x0006, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A1
	Internal Connector Type: None
	External Reference Designator: TV Out
	External Connector Type: Mini Centronics Type-14
	Port Type: Other

Handle 0x0007, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A2A
	Internal Connector Type: None
	External Reference Designator: COM A
	External Connector Type: DB-9 male
	Port Type: Serial Port 16550A Compatible

Handle 0x0008, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2A2B
	Internal Connector Type: None
	External Reference Designator: Video
	External Connector Type: DB-15 female
	Port Type: Video Port

Handle 0x0009, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB1
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000A, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB2
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000B, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3A1
	Internal Connector Type: None
	External Reference Designator: USB3
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000C, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9A1 - TPM HDR
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x000D, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9C1 - PCIE DOCKING CONN
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x000E, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2B3 - CPU FAN
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x000F, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J6C2 - EXT HDMI
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0010, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J3C1 - GMCH FAN
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0011, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1D1 - ITP
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0012, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9E2 - MDC INTPSR
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0013, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9E4 - MDC INTPSR
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0014, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9E3 - LPC HOT DOCKING
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0015, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9E1 - SCAN MATRIX
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0016, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J9G1 - LPC SIDE BAND
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0017, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J8F1 - UNIFIED
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0018, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J6F1 - LVDS
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x0019, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2F1 - LAI FAN
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x001A, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J2G1 - GFX VID
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x001B, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: J1G6 - AC JACK
	Internal Connector Type: Other
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: Other

Handle 0x001C, DMI type 9, 17 bytes
System Slot Information
	Designation: J6B2
	Type: x16 PCI Express
	Current Usage: Available
	Length: Long
	ID: 0
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:01.0

Handle 0x001D, DMI type 10, 10 bytes
On Board Device 1 Information
	Type: Video
	Status: Enabled
	Description:  Intel(R) HD Graphics Device
On Board Device 2 Information
	Type: Ethernet
	Status: Enabled
	Description:  Intel(R) WGI218V Gigabit Network Device
On Board Device 3 Information
	Type: Sound
	Status: Disabled
	Description:  Realtek High Definition Audio Device

Handle 0x001E, DMI type 11, 5 bytes
OEM Strings
	String 1: To Be Filled By O.E.M.

Handle 0x001F, DMI type 12, 5 bytes
System Configuration Options
	Option 1: To Be Filled By O.E.M.

Handle 0x0020, DMI type 24, 5 bytes
Hardware Security
	Power-On Password Status: Disabled
	Keyboard Password Status: Disabled
	Administrator Password Status: Disabled
	Front Panel Reset Status: Disabled

Handle 0x0021, DMI type 32, 20 bytes
System Boot Information
	Status: No errors detected

Handle 0x0022, DMI type 34, 11 bytes
Management Device
	Description: LM78-1
	Type: LM78
	Address: 0x00000000
	Address Type: I/O Port

Handle 0x0023, DMI type 26, 22 bytes
Voltage Probe
	Description: LM78A
	Location: <OUT OF SPEC>
	Status: <OUT OF SPEC>
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0024, DMI type 36, 16 bytes
Management Device Threshold Data
	Lower Non-critical Threshold: 1
	Upper Non-critical Threshold: 2
	Lower Critical Threshold: 3
	Upper Critical Threshold: 4
	Lower Non-recoverable Threshold: 5
	Upper Non-recoverable Threshold: 6

Handle 0x0025, DMI type 35, 11 bytes
Management Device Component
	Description: To Be Filled By O.E.M.
	Management Device Handle: 0x0022
	Component Handle: 0x0022
	Threshold Handle: 0x0023

Handle 0x0026, DMI type 28, 22 bytes
Temperature Probe
	Description: LM78A
	Location: <OUT OF SPEC>
	Status: <OUT OF SPEC>
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0027, DMI type 36, 16 bytes
Management Device Threshold Data
	Lower Non-critical Threshold: 1
	Upper Non-critical Threshold: 2
	Lower Critical Threshold: 3
	Upper Critical Threshold: 4
	Lower Non-recoverable Threshold: 5
	Upper Non-recoverable Threshold: 6

Handle 0x0028, DMI type 35, 11 bytes
Management Device Component
	Description: To Be Filled By O.E.M.
	Management Device Handle: 0x0022
	Component Handle: 0x0025
	Threshold Handle: 0x0026

Handle 0x0029, DMI type 27, 15 bytes
Cooling Device
	Temperature Probe Handle: 0x0026
	Type: <OUT OF SPEC>
	Status: <OUT OF SPEC>
	Cooling Unit Group: 1
	OEM-specific Information: 0x00000000
	Nominal Speed: Unknown Or Non-rotating
	Description: Cooling Dev 1

Handle 0x002A, DMI type 36, 16 bytes
Management Device Threshold Data
	Lower Non-critical Threshold: 1
	Upper Non-critical Threshold: 2
	Lower Critical Threshold: 3
	Upper Critical Threshold: 4
	Lower Non-recoverable Threshold: 5
	Upper Non-recoverable Threshold: 6

Handle 0x002B, DMI type 35, 11 bytes
Management Device Component
	Description: To Be Filled By O.E.M.
	Management Device Handle: 0x0022
	Component Handle: 0x0028
	Threshold Handle: 0x0029

Handle 0x002C, DMI type 27, 15 bytes
Cooling Device
	Temperature Probe Handle: 0x0026
	Type: <OUT OF SPEC>
	Status: <OUT OF SPEC>
	Cooling Unit Group: 1
	OEM-specific Information: 0x00000000
	Nominal Speed: Unknown Or Non-rotating
	Description: Not Specified

Handle 0x002D, DMI type 36, 16 bytes
Management Device Threshold Data
	Lower Non-critical Threshold: 1
	Upper Non-critical Threshold: 2
	Lower Critical Threshold: 3
	Upper Critical Threshold: 4
	Lower Non-recoverable Threshold: 5
	Upper Non-recoverable Threshold: 6

Handle 0x002E, DMI type 35, 11 bytes
Management Device Component
	Description: To Be Filled By O.E.M.
	Management Device Handle: 0x0022
	Component Handle: 0x002B
	Threshold Handle: 0x002C

Handle 0x002F, DMI type 29, 22 bytes
Electrical Current Probe
	Description: ABC
	Location: <OUT OF SPEC>
	Status: <OUT OF SPEC>
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0030, DMI type 36, 16 bytes
Management Device Threshold Data

Handle 0x0031, DMI type 35, 11 bytes
Management Device Component
	Description: To Be Filled By O.E.M.
	Management Device Handle: 0x0022
	Component Handle: 0x002E
	Threshold Handle: 0x002C

Handle 0x0032, DMI type 26, 22 bytes
Voltage Probe
	Description: LM78A
	Location: Power Unit
	Status: OK
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0033, DMI type 28, 22 bytes
Temperature Probe
	Description: LM78A
	Location: Power Unit
	Status: OK
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0034, DMI type 27, 15 bytes
Cooling Device
	Temperature Probe Handle: 0x0033
	Type: Power Supply Fan
	Status: OK
	Cooling Unit Group: 1
	OEM-specific Information: 0x00000000
	Nominal Speed: Unknown Or Non-rotating
	Description: Cooling Dev 1

Handle 0x0035, DMI type 29, 22 bytes
Electrical Current Probe
	Description: ABC
	Location: Power Unit
	Status: OK
	Maximum Value: Unknown
	Minimum Value: Unknown
	Resolution: Unknown
	Tolerance: Unknown
	Accuracy: Unknown
	OEM-specific Information: 0x00000000
	Nominal Value: Unknown

Handle 0x0036, DMI type 39, 22 bytes
System Power Supply
	Power Unit Group: 1
	Location: To Be Filled By O.E.M.
	Name: To Be Filled By O.E.M.
	Manufacturer: To Be Filled By O.E.M.
	Serial Number: To Be Filled By O.E.M.
	Asset Tag: To Be Filled By O.E.M.
	Model Part Number: To Be Filled By O.E.M.
	Revision: To Be Filled By O.E.M.
	Max Power Capacity: Unknown
	Status: Present, OK
	Type: Switching
	Input Voltage Range Switching: Auto-switch
	Plugged: Yes
	Hot Replaceable: No
	Input Voltage Probe Handle: 0x0032
	Cooling Device Handle: 0x0034
	Input Current Probe Handle: 0x0035

Handle 0x0037, DMI type 41, 11 bytes
Onboard Device
	Reference Designation:  CPU1
	Type: Video
	Status: Enabled
	Type Instance: 1
	Bus Address: 0000:00:02.0

Handle 0x0038, DMI type 41, 11 bytes
Onboard Device
	Reference Designation:  ILAN
	Type: Ethernet
	Status: Enabled
	Type Instance: 1
	Bus Address: 0000:00:19.0

Handle 0x0039, DMI type 41, 11 bytes
Onboard Device
	Reference Designation:  AUDIO1
	Type: Sound
	Status: Disabled
	Type Instance: 1
	Bus Address: 0000:00:1b.0

Handle 0x003A, DMI type 4, 42 bytes
Processor Information
	Socket Designation: CPU 1
	Type: Central Processor
	Family: Core i3
	Manufacturer: Intel(R) Corp.
	ID: 51 06 04 00 FF FB EB BF
	Signature: Type 0, Family 6, Model 69, Stepping 1
	Flags:
		FPU (Floating-point unit on-chip)
		VME (Virtual mode extension)
		DE (Debugging extension)
		PSE (Page size extension)
		TSC (Time stamp counter)
		MSR (Model specific registers)
		PAE (Physical address extension)
		MCE (Machine check exception)
		CX8 (CMPXCHG8 instruction supported)
		APIC (On-chip APIC hardware supported)
		SEP (Fast system call)
		MTRR (Memory type range registers)
		PGE (Page global enable)
		MCA (Machine check architecture)
		CMOV (Conditional move instruction supported)
		PAT (Page attribute table)
		PSE-36 (36-bit page size extension)
		CLFSH (CLFLUSH instruction supported)
		DS (Debug store)
		ACPI (ACPI supported)
		MMX (MMX technology supported)
		FXSR (FXSAVE and FXSTOR instructions supported)
		SSE (Streaming SIMD extensions)
		SSE2 (Streaming SIMD extensions 2)
		SS (Self-snoop)
		HTT (Multi-threading)
		TM (Thermal monitor supported)
		PBE (Pending break enabled)
	Version: Intel(R) Core(TM) i3-4010U CPU @ 1.70GHz
	Voltage: 1.2 V
	External Clock: 100 MHz
	Max Speed: 1700 MHz
	Current Speed: 1700 MHz
	Status: Populated, Enabled
	Upgrade: <OUT OF SPEC>
	L1 Cache Handle: 0x003C
	L2 Cache Handle: 0x003B
	L3 Cache Handle: 0x003D
	Serial Number: Not Specified
	Asset Tag: Fill By OEM
	Part Number: Fill By OEM
	Core Count: 2
	Core Enabled: 2
	Thread Count: 4
	Characteristics:
		64-bit capable

Handle 0x003B, DMI type 7, 19 bytes
Cache Information
	Socket Designation: CPU Internal L2
	Configuration: Enabled, Not Socketed, Level 2
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 512 kB
	Maximum Size: 512 kB
	Supported SRAM Types:
		Unknown
	Installed SRAM Type: Unknown
	Speed: Unknown
	Error Correction Type: Single-bit ECC
	System Type: Unified
	Associativity: 8-way Set-associative

Handle 0x003C, DMI type 7, 19 bytes
Cache Information
	Socket Designation: CPU Internal L1
	Configuration: Enabled, Not Socketed, Level 1
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 128 kB
	Maximum Size: 128 kB
	Supported SRAM Types:
		Unknown
	Installed SRAM Type: Unknown
	Speed: Unknown
	Error Correction Type: Single-bit ECC
	System Type: Other
	Associativity: 8-way Set-associative

Handle 0x003D, DMI type 7, 19 bytes
Cache Information
	Socket Designation: CPU Internal L3
	Configuration: Enabled, Not Socketed, Level 3
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 3072 kB
	Maximum Size: 3072 kB
	Supported SRAM Types:
		Unknown
	Installed SRAM Type: Unknown
	Speed: Unknown
	Error Correction Type: Single-bit ECC
	System Type: Unified
	Associativity: 12-way Set-associative

Handle 0x003E, DMI type 16, 23 bytes
Physical Memory Array
	Location: System Board Or Motherboard
	Use: System Memory
	Error Correction Type: None
	Maximum Capacity: 16 GB
	Error Information Handle: Not Provided
	Number Of Devices: 2

Handle 0x003F, DMI type 17, 40 bytes
Memory Device
	Array Handle: 0x003E
	Error Information Handle: Not Provided
	Total Width: 64 bits
	Data Width: 64 bits
	Size: 8192 MB
	Form Factor: SODIMM
	Set: None
	Locator: DIMM 1
	Bank Locator: Channel A Slot 0
	Type: DDR3
	Type Detail: Synchronous
	Speed: 1600 MHz
	Manufacturer: Samsung
	Serial Number: 057166068222
	Asset Tag: 9876543210
	Part Number: M471B1G73DM0-YK0
	Rank: 2
	Configured Clock Speed: 1600 MHz
	Minimum Voltage:  1.35 V
	Maximum Voltage:  1.5 V
	Configured Voltage:  1.35 V

Handle 0x0040, DMI type 20, 35 bytes
Memory Device Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x001FFFFFFFF
	Range Size: 8 GB
	Physical Device Handle: 0x003F
	Memory Array Mapped Address Handle: 0x0043
	Partition Row Position: Unknown
	Interleave Position: Unknown
	Interleaved Data Depth: Unknown

Handle 0x0041, DMI type 17, 40 bytes
Memory Device
	Array Handle: 0x003E
	Error Information Handle: Not Provided
	Total Width: 64 bits
	Data Width: 64 bits
	Size: 8192 MB
	Form Factor: SODIMM
	Set: None
	Locator: DIMM 2
	Bank Locator: Channel B Slot 0
	Type: DDR3
	Type Detail: Synchronous
	Speed: 1600 MHz
	Manufacturer: Samsung
	Serial Number: 057166068210
	Asset Tag: 9876543210
	Part Number: M471B1G73DM0-YK0
	Rank: 2
	Configured Clock Speed: 1600 MHz
	Minimum Voltage:  1.35 V
	Maximum Voltage:  1.5 V
	Configured Voltage:  1.35 V

Handle 0x0042, DMI type 20, 35 bytes
Memory Device Mapped Address
	Starting Address: 0x00200000000
	Ending Address: 0x003FFFFFFFF
	Range Size: 8 GB
	Physical Device Handle: 0x0041
	Memory Array Mapped Address Handle: 0x0043
	Partition Row Position: Unknown
	Interleave Position: Unknown
	Interleaved Data Depth: Unknown

Handle 0x0043, DMI type 19, 31 bytes
Memory Array Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x003FFFFFFFF
	Range Size: 16 GB
	Physical Array Handle: 0x003E
	Partition Width: 2

Handle 0x0054, DMI type 136, 6 bytes
OEM-specific Type
	Header and Data:
		88 06 54 00 5A 5A

Handle 0x0055, DMI type 131, 64 bytes
OEM-specific Type
	Header and Data:
		83 40 55 00 31 00 00 00 00 00 00 00 00 00 00 00
		F8 00 43 9C 00 00 00 00 01 00 00 00 05 00 09 00
		AA 06 0D 00 00 00 00 00 C8 00 59 15 00 00 00 00
		00 00 00 00 26 00 00 00 76 50 72 6F 00 00 00 00

Handle 0x0057, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x1 PCI Express 2 x1
	Current Usage: Available
	Length: Short
	ID: 7
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.5

Handle 0x0058, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x1 PCI Express 2 x1
	Current Usage: Available
	Length: Short
	ID: 5
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.6

Handle 0x0059, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x4 PCI Express 2 x4
	Current Usage: Available
	Length: Short
	ID: 4
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.1

Handle 0x005A, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 3
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:00.0

Handle 0x005B, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 2
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:01.0

Handle 0x005C, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 1
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:02.0

Handle 0x005D, DMI type 13, 22 bytes
BIOS Language Information
	Language Description Format: Long
	Installable Languages: 1
		en|US|iso8859-1
	Currently Installed Language: en|US|iso8859-1

Handle 0x0060, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x1 PCI Express 2 x1
	Current Usage: Available
	Length: Short
	ID: 7
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.5

Handle 0x0061, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x1 PCI Express 2 x1
	Current Usage: Available
	Length: Short
	ID: 5
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.6

Handle 0x0062, DMI type 9, 17 bytes
System Slot Information
	Designation: PCIeSlot
	Type: x4 PCI Express 2 x4
	Current Usage: Available
	Length: Short
	ID: 4
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:00:1c.1

Handle 0x0063, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 3
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:00.0

Handle 0x0064, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 2
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:01.0

Handle 0x0065, DMI type 9, 17 bytes
System Slot Information
	Designation: PCI Slot
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 1
	Characteristics:
		3.3 V is provided
		Opening is shared
		PME signal is supported
	Bus Address: 0000:ff:02.0

Handle 0x0066, DMI type 127, 4 bytes
End Of Table
"""

val nongnu = """
# dmidecode 2.8
SMBIOS 2.3 present.
49 structures occupying 1383 bytes.
Table at 0x000F2940.

Handle 0x0000, DMI type 0, 20 bytes
BIOS Information
	Vendor: Award Software, Inc.
	Version: ASUS A7V133-C ACPI BIOS Revision 1009
	Release Date: 04/23/2002
	Address: 0xF0000
	Runtime Size: 64 kB
	ROM Size: 256 kB
	Characteristics:
		PCI is supported
		PNP is supported
		APM is supported
		BIOS is upgradeable
		BIOS shadowing is allowed
		ESCD support is available
		Boot from CD is supported
		Selectable boot is supported
		BIOS ROM is socketed
		EDD is supported
		5.25"/360 KB floppy services are supported (int 13h)
		5.25"/1.2 MB floppy services are supported (int 13h)
		3.5"/720 KB floppy services are supported (int 13h)
		3.5"/2.88 MB floppy services are supported (int 13h)
		Print screen service is supported (int 5h)
		8042 keyboard services are supported (int 9h)
		Serial services are supported (int 14h)
		Printer services are supported (int 17h)
		CGA/mono video services are supported (int 10h)
		ACPI is supported
		USB legacy is supported
		AGP is supported

Handle 0x0001, DMI type 1, 25 bytes
System Information
	Manufacturer: System Manufacturer
	Product Name: System Name
	Version: System Version
	Serial Number: SYS-1234567890
	UUID: Not Settable
	Wake-up Type: Power Switch

Handle 0x0002, DMI type 2, 8 bytes
Base Board Information
	Manufacturer: ASUSTeK Computer INC.
	Product Name: A7V133-C
	Version: REV 1.xx
	Serial Number: xxxxxxxxxxx

Handle 0x0003, DMI type 3, 17 bytes
Chassis Information
	Manufacturer: Chassis Manufacture
	Type: Tower
	Lock: Not Present
	Version: Chassis Version
	Serial Number: Chassis Serial Number
	Asset Tag: Asset-1234567890
	Boot-up State: Safe
	Power Supply State: Safe
	Thermal State: Safe
	Security Status: Unknown
	OEM Information: 0x00000000

Handle 0x0004, DMI type 4, 32 bytes
Processor Information
	Socket Designation: SOCKET A
	Type: Central Processor
	Family: Other
	Manufacturer: AuthenticAMD
	ID: 42 06 00 00 FF F9 83 01
	Signature: Family 6, Model 4, Stepping 2
	Flags:
		FPU (Floating-point unit on-chip)
		VME (Virtual mode extension)
		DE (Debugging extension)
		PSE (Page size extension)
		TSC (Time stamp counter)
		MSR (Model specific registers)
		PAE (Physical address extension)
		MCE (Machine check exception)
		CX8 (CMPXCHG8 instruction supported)
		SEP (Fast system call)
		MTRR (Memory type range registers)
		PGE (Page global enable)
		MCA (Machine check architecture)
		CMOV (Conditional move instruction supported)
		PAT (Page attribute table)
		PSE-36 (36-bit page size extension)
		MMX (MMX technology supported)
		FXSR (Fast floating-point save and restore)
	Version: AMD Athlon(TM) Processor
	Voltage: 1.7 V
	External Clock: 133 MHz
	Max Speed: 1800 MHz
	Current Speed: 1200 MHz
	Status: Populated, Enabled
	Upgrade: Other
	L1 Cache Handle: 0x0009
	L2 Cache Handle: 0x000A
	L3 Cache Handle: Not Provided

Handle 0x0005, DMI type 5, 22 bytes
Memory Controller Information
	Error Detecting Method: 64-bit ECC
	Error Correcting Capabilities:
		None
	Supported Interleave: One-way Interleave
	Current Interleave: One-way Interleave
	Maximum Memory Module Size: 1024 MB
	Maximum Total Memory Size: 3072 MB
	Supported Speeds:
		Other
	Supported Memory Types:
		Other
		DIMM
		SDRAM
	Memory Module Voltage: 3.3 V
	Associated Memory Slots: 3
		0x0006
		0x0007
		0x0008
	Enabled Error Correcting Capabilities:
		Unknown

Handle 0x0006, DMI type 6, 12 bytes
Memory Module Information
	Socket Designation: DIMM 1
	Bank Connections: 0 1
	Current Speed: Unknown
	Type: Other DIMM SDRAM
	Installed Size: 256 MB (Double-bank Connection)
	Enabled Size: 256 MB (Double-bank Connection)
	Error Status: OK

Handle 0x0007, DMI type 6, 12 bytes
Memory Module Information
	Socket Designation: DIMM 2
	Bank Connections: 2 3
	Current Speed: Unknown
	Type: Other DIMM SDRAM
	Installed Size: 256 MB (Double-bank Connection)
	Enabled Size: 256 MB (Double-bank Connection)
	Error Status: OK

Handle 0x0008, DMI type 6, 12 bytes
Memory Module Information
	Socket Designation: DIMM 3
	Bank Connections: 4 5
	Current Speed: Unknown
	Type: Other DIMM SDRAM
	Installed Size: Not Installed
	Enabled Size: Not Installed
	Error Status: OK

Handle 0x0009, DMI type 7, 19 bytes
Cache Information
	Socket Designation: L1 Cache
	Configuration: Enabled, Not Socketed, Level 1
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 128 KB
	Maximum Size: 128 KB
	Supported SRAM Types:
		Synchronous
	Installed SRAM Type: Synchronous
	Speed: Unknown
	Error Correction Type: Unknown
	System Type: Data
	Associativity: 4-way Set-associative

Handle 0x000A, DMI type 7, 19 bytes
Cache Information
	Socket Designation: L2 Cache
	Configuration: Enabled, Not Socketed, Level 2
	Operational Mode: Write Back
	Location: Internal
	Installed Size: 256 KB
	Maximum Size: 8192 KB
	Supported SRAM Types:
		Pipeline Burst
		Synchronous
	Installed SRAM Type: Pipeline Burst Synchronous
	Speed: Unknown
	Error Correction Type: Unknown
	System Type: Data
	Associativity: 4-way Set-associative

Handle 0x000B, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: PRIMARY IDE/HDD
	Internal Connector Type: On Board IDE
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: None

Handle 0x000C, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: SECONDARY IDE/HDD
	Internal Connector Type: On Board IDE
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: None

Handle 0x000D, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: FLOPPY
	Internal Connector Type: On Board Floppy
	External Reference Designator: Not Specified
	External Connector Type: None
	Port Type: None

Handle 0x000E, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: USB1
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x000F, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: USB2
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0010, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: USB3
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0011, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: USB4
	External Connector Type: Access Bus (USB)
	Port Type: USB

Handle 0x0012, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: PS/2 Keybaord
	External Connector Type: PS/2
	Port Type: Keyboard Port

Handle 0x0013, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: PS/2 Mouse
	External Connector Type: PS/2
	Port Type: Mouse Port

Handle 0x0014, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Parallel Port
	External Connector Type: DB-25 female
	Port Type: Parallel Port ECP/EPP

Handle 0x0015, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Serial Port 1
	External Connector Type: DB-9 male
	Port Type: Serial Port 16550 Compatible

Handle 0x0016, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Serial Port 2
	External Connector Type: DB-9 male
	Port Type: Serial Port 16550 Compatible

Handle 0x0017, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Joystick Port
	External Connector Type: DB-15 female
	Port Type: Joystick Port

Handle 0x0018, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: MIDI Port
	External Connector Type: DB-15 female
	Port Type: MIDI Port

Handle 0x0019, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Line In Jack
	External Connector Type: Mini Jack (headphones)
	Port Type: Audio Port

Handle 0x001A, DMI type 8, 9 bytes
Port Connector Information
	Internal Reference Designator: Not Specified
	Internal Connector Type: None
	External Reference Designator: Video Port
	External Connector Type: Mini Jack (headphones)
	Port Type: Video Port

Handle 0x001B, DMI type 9, 13 bytes
System Slot Information
	Designation: PCI 1
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		PME signal is supported

Handle 0x001C, DMI type 9, 13 bytes
System Slot Information
	Designation: PCI 2
	Type: 32-bit PCI
	Current Usage: In Use
	Length: Short
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		PME signal is supported

Handle 0x001D, DMI type 9, 13 bytes
System Slot Information
	Designation: PCI 3
	Type: 32-bit PCI
	Current Usage: In Use
	Length: Short
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		PME signal is supported

Handle 0x001E, DMI type 9, 13 bytes
System Slot Information
	Designation: PCI 4
	Type: 32-bit PCI
	Current Usage: Available
	Length: Short
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		PME signal is supported

Handle 0x001F, DMI type 9, 13 bytes
System Slot Information
	Designation: PCI 5
	Type: 32-bit PCI
	Current Usage: In Use
	Length: Short
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided
		PME signal is supported

Handle 0x0020, DMI type 9, 13 bytes
System Slot Information
	Designation: AGP
	Type: 32-bit AGP 4x
	Current Usage: In Use
	Length: Long
	ID: 0
	Characteristics:
		5.0 V is provided
		3.3 V is provided

Handle 0x0021, DMI type 9, 13 bytes
System Slot Information
	Designation: AMR
	Type: 32-bit I/O Riser Card
	Current Usage: In Use
	Length: Long
	Characteristics:
		5.0 V is provided
		3.3 V is provided

Handle 0x0022, DMI type 10, 6 bytes
On Board Device Information
	Type: Other
	Status: Disabled
	Description: Promise PDC20265

Handle 0x0023, DMI type 11, 5 bytes
OEM Strings
	String 1: 0
	String 2: 0

Handle 0x0024, DMI type 13, 22 bytes
BIOS Language Information
	Installable Languages: 1
		en|US|iso8859-1
	Currently Installed Language: en|US|iso8859-1

Handle 0x0025, DMI type 14, 14 bytes
Group Associations
	Name: Cpu Module
	Items: 3
		0x0004 (Processor)
		0x0009 (Cache)
		0x000A (Cache)

Handle 0x0026, DMI type 14, 29 bytes
Group Associations
	Name: Memory Module Set
	Items: 8
		0x0027 (Physical Memory Array)
		0x0028 (Memory Device)
		0x002C (Memory Device Mapped Address)
		0x0029 (Memory Device)
		0x002D (Memory Device Mapped Address)
		0x002A (Memory Device)
		0x002E (Memory Device Mapped Address)
		0x002B (Memory Array Mapped Address)

Handle 0x0027, DMI type 16, 15 bytes
Physical Memory Array
	Location: System Board Or Motherboard
	Use: System Memory
	Error Correction Type: None
	Maximum Capacity: 1536 MB
	Error Information Handle: Not Provided
	Number Of Devices: 3

Handle 0x0028, DMI type 17, 23 bytes
Memory Device
	Array Handle: 0x0027
	Error Information Handle: No Error
	Total Width: 72 bits
	Data Width: 64 bits
	Size: 256 MB
	Form Factor: DIMM
	Set: 1
	Locator: DIMM 1
	Bank Locator: Not Specified
	Type: DRAM
	Type Detail: Synchronous
	Speed: Unknown

Handle 0x0029, DMI type 17, 23 bytes
Memory Device
	Array Handle: 0x0027
	Error Information Handle: No Error
	Total Width: 72 bits
	Data Width: 64 bits
	Size: 256 MB
	Form Factor: DIMM
	Set: 2
	Locator: DIMM 2
	Bank Locator: Not Specified
	Type: DRAM
	Type Detail: Synchronous
	Speed: Unknown

Handle 0x002A, DMI type 17, 23 bytes
Memory Device
	Array Handle: 0x0027
	Error Information Handle: No Error
	Total Width: Unknown
	Data Width: Unknown
	Size: No Module Installed
	Form Factor: DIMM
	Set: 3
	Locator: DIMM 3
	Bank Locator: Not Specified
	Type: DRAM
	Type Detail: Synchronous
	Speed: Unknown

Handle 0x002B, DMI type 19, 15 bytes
Memory Array Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x080000003FF
	Range Size: 536870913 kB
	Physical Array Handle: 0x0027
	Partition Width: 0

Handle 0x002C, DMI type 20, 19 bytes
Memory Device Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x0000FFFFFFF
	Range Size: 256 MB
	Physical Device Handle: 0x0028
	Memory Array Mapped Address Handle: 0x002B
	Partition Row Position: 1

Handle 0x002D, DMI type 20, 19 bytes
Memory Device Mapped Address
	Starting Address: 0x00010000000
	Ending Address: 0x0001FFFFFFF
	Range Size: 256 MB
	Physical Device Handle: 0x0029
	Memory Array Mapped Address Handle: 0x002B
	Partition Row Position: 2

Handle 0x002E, DMI type 126, 19 bytes
Inactive

Handle 0x002F, DMI type 32, 11 bytes
System Boot Information
	Status: No errors detected

Handle 0x0030, DMI type 127, 4 bytes
End Of Table
"""

val qemuKvm = """
# dmidecode 2.12
SMBIOS 2.8 present.
9 structures occupying 387 bytes.
Table at 0x000F0E80.

Handle 0x0000, DMI type 0, 24 bytes
BIOS Information
	Vendor: SeaBIOS
	Version: 1.7.5-20140709_153950-
	Release Date: 04/01/2014
	Address: 0xE8000
	Runtime Size: 96 kB
	ROM Size: 64 kB
	Characteristics:
		BIOS characteristics not supported
		Targeted content distribution is supported
	BIOS Revision: 0.0

Handle 0x0100, DMI type 1, 27 bytes
System Information
	Manufacturer: QEMU
	Product Name: Standard PC (i440FX + PIIX, 1996)
	Version: pc-i440fx-2.1
	Serial Number: Not Specified
	UUID: 99163626-EDCF-FB4C-A81C-A7FD9EAA058E
	Wake-up Type: Power Switch
	SKU Number: Not Specified
	Family: Not Specified

Handle 0x0300, DMI type 3, 21 bytes
Chassis Information
	Manufacturer: QEMU
	Type: Other
	Lock: Not Present
	Version: pc-i440fx-2.1
	Serial Number: Not Specified
	Asset Tag: Not Specified
	Boot-up State: Safe
	Power Supply State: Safe
	Thermal State: Safe
	Security Status: Unknown
	OEM Information: 0x00000000
	Height: Unspecified
	Number Of Power Cords: Unspecified
	Contained Elements: 0

Handle 0x0400, DMI type 4, 42 bytes
Processor Information
	Socket Designation: CPU 0
	Type: Central Processor
	Family: Other
	Manufacturer: QEMU
	ID: A1 06 02 00 FD FB 8B 07
	Version: pc-i440fx-2.1
	Voltage: Unknown
	External Clock: Unknown
	Max Speed: Unknown
	Current Speed: Unknown
	Status: Populated, Enabled
	Upgrade: Other
	L1 Cache Handle: Not Provided
	L2 Cache Handle: Not Provided
	L3 Cache Handle: Not Provided
	Serial Number: Not Specified
	Asset Tag: Not Specified
	Part Number: Not Specified
	Core Count: 1
	Core Enabled: 1
	Thread Count: 1
	Characteristics: None

Handle 0x1000, DMI type 16, 23 bytes
Physical Memory Array
	Location: Other
	Use: System Memory
	Error Correction Type: Multi-bit ECC
	Maximum Capacity: 1 GB
	Error Information Handle: Not Provided
	Number Of Devices: 1

Handle 0x1100, DMI type 17, 40 bytes
Memory Device
	Array Handle: 0x1000
	Error Information Handle: Not Provided
	Total Width: Unknown
	Data Width: Unknown
	Size: 1024 MB
	Form Factor: DIMM
	Set: None
	Locator: DIMM 0
	Bank Locator: Not Specified
	Type: RAM
	Type Detail: Other
	Speed: Unknown
	Manufacturer: QEMU
	Serial Number: Not Specified
	Asset Tag: Not Specified
	Part Number: Not Specified
	Rank: Unknown
	Configured Clock Speed: Unknown
	Minimum voltage:  Unknown
	Maximum voltage:  Unknown
	Configured voltage:  Unknown

Handle 0x1300, DMI type 19, 31 bytes
Memory Array Mapped Address
	Starting Address: 0x00000000000
	Ending Address: 0x0003FFFFFFF
	Range Size: 1 GB
	Physical Array Handle: 0x1000
	Partition Width: 1

Handle 0x2000, DMI type 32, 11 bytes
System Boot Information
	Status: No errors detected

Handle 0x7F00, DMI type 127, 4 bytes
End Of Table

"""