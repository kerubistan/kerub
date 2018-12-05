
[![FreeBSD](https://img.shields.io/badge/FreeBSD-10+-red.svg)](http://freebsd.org)
[![CentOs](https://img.shields.io/badge/CentOs-6+-blue.svg)](https://centos.org/)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-14+-red.svg)](http://ubuntu.com)
[![OpenSUSE](https://img.shields.io/badge/OpenSUSE-13+-green.svg)](http://opensuse.org)

kerub
=====

![Logo](https://raw.githubusercontent.com/kerubistan/kerub/master/src/main/webapp/img/kerub.png)

A lightweight prototype IaaS application


Objective
=======

The objective of this project is to demonstrate some technologies and solutions in the IaaS field.
Such solutions are:
 * User expectations - the user sets the service level (SLA) of the virtual resources by adding expectations
 * Scheduling as an optimization/constraint enforcement problem - which means SLA defined by user is checked on each event and the required steps are taken to maintain it
 * No host-cluster - this construct was created only to help human-operators to do scheduling - a bad use of payed work-hours

Architectural shift:
 * No host agent
 * Event driven, no poll
 * NoSQL document store for VM data
 * Simplicity in deployment and development
 * Minimal hardware requirements

Put rare features to use:
 * KSM

Despite all of the above objectives:
 * it is not for architecture astronauts
 * not a playground for design patterns
 * must be simple and readable
 * must be tested and working well

Status
=======

Kerub is in research/development phase, however you should be able to run and use some minimalistic functionality:
 - storage management with LVM, GVinum (FreeBSD) and filesystems
 - storage protocols like nfs and iscsi
 - KVM hypervisor
 - SPICE virtual console

Tests
=====

Some tests are included in the application, both unit and integration tests, these tests can simulate workload and
run very quick (the full build is around 5 minutes on a dual-core i7 CPU). However these tests can not cover all aspects,
for example we can not have a real host with an actual operating system installed on it, the command executions are faked, etc.

To fill this gap, there is another package only for tests called [kerub-ext-tests](https://github.com/kerubistan/kerub-ext-tests).
This however runs against packaged (RPM, DEB) versions of the application, sophisticated to setup, requires hypervisor 
hardware and software and a full run takes several hours.

There is a second separate test package for microbenchmarks: [kerub-microbenchmarx](https://github.com/kerubistan/kerub-microbenchmarx)
This is a JMH-based benchmark collection to help tuning (mostly but only) the planner.

How to get started
=======

``` 
git clone https://github.com/kerubistan/kerub/
cd kerub
mvn jetty:run
```

Open http://localhost:8080/ in your favourite browser, and you have a controller running.

First of all, you will need a few (at least one) host to work with. This can be a virtual machine in test environments if nested virtualization is enabled, or a physical server.
The host needs a few software to work with:
 * an operating system (see above)
 * all hosts need a working ssh daemon, root must be able to connect remotely with public key authentication
 * storage software, like lvm, zfs or gvinum
 * storage protocols, like iscsi
 * virtualization software, like kvm+qemu with libvirt (virtualbox, xen and others under development)
 * monitoring and hardware discovery software, typically the ones found in any linux distribution

Kerub will find whatever is installed.

OS Packages
===========

| OS / Distribution | packaging project 										   | status 	 |
|-------------------|--------------------------------------------------------------|-------------| 
| Fedora			| [kerub-fedora](https://github.com/kerubistan/kerub-fedora)   | maintained  |
| CentOS			| [kerub-centos](https://github.com/kerubistan/kerub-centos)   | maintained  |
| ubuntu			| [kerub-ubuntu](https://github.com/kerubistan/kerub-ubuntu)   | development |
| openSUSE			| [kerub-openuse](https://github.com/kerubistan/kerub-opensuse)| maintained  |

