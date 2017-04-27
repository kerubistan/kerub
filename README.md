
[![Kotlin](https://img.shields.io/badge/kotlin-1.1.2-blue.svg)](http://kotlinlang.org)
[![FreeBSD](https://img.shields.io/badge/FreeBSD-10+-red.svg)](http://freebsd.org)
[![Fedora](https://img.shields.io/badge/Fedora-20+-blue.svg)](https://getfedora.org/)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-14+-red.svg)](http://ubuntu.com)
[![OpenSUSE](https://img.shields.io/badge/OpenSUSE-13+-green.svg)](http://opensuse.org)

[![build](https://img.shields.io/travis/kerubistan/kerub.svg)](https://travis-ci.org/kerubistan/kerub)

kerub
=====

![Logo](https://raw.githubusercontent.com/K0zka/kerub/master/src/main/webapp/img/kerub.png)

A lightweight prototype IaaS application


Objective
=======

The objective of this project is to demonstrate some technologies and solutions in the IaaS field.
Such solutions are:
 * User expectations
 * Scheduling as an optimization/constraint enforcement problem
 * No host-cluster

Architectural shift:
 * No host agent
 * Event driven, no poll
 * NoSQL document store for VM data
 * KSM
 * Simplicity in deployment and development
 * Minimal hardware requirements

Despite all of the above objectives:
 * it is not for architecture astronauts
 * must be simple and readable

Status
=======

Kerub is in research/development phase, however you should be able to run and use some minimalistic functionality.

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

