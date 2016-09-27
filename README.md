[![Kotlin](https://img.shields.io/badge/kotlin-1.0.4-blue.svg)](http://kotlinlang.org)
[![FreeBSD](https://img.shields.io/badge/FreeBSD-10+-red.svg)](http://freebsd.org)
[![Fedora](https://img.shields.io/badge/Fedora-20+-blue.svg)](https://getfedora.org/)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-14+-red.svg)](http://freebsd.org)
[![OpenSUSE](https://img.shields.io/badge/OpenSUSE-13+-green.svg)](http://freebsd.org)


kerub
=====

![Logo](https://raw.githubusercontent.com/K0zka/kerub/master/src/main/webapp/img/kerub.png)

A lightweight prototype IaaS application


Purpose
=======

The purpose of this project is to demonstrate some technologies and solutions in the IaaS field.
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
