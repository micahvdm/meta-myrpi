SUMMARY = "Raspberry Pi kernel tweaks - using an external kernel w preempt_rt"
LICENSE = "MIT"

LINUX_VERSION ?= "5.15.65"
LINUX_RPI_BRANCH ?= "rpi-5.15.65-rt"

SRCREV = "c91efd15b37d08fd46e899d1523c47dc6ad1bf9d"

require linux-raspberrypi_5.15.inc
