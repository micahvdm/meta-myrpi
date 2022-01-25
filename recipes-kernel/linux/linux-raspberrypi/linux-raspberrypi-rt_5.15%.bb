SUMMARY = "Raspberry Pi kernel tweaks - using an external kernel w preempt_rt"
LICENSE = "MIT"

LINUX_VERSION ?= "5.15.12"
LINUX_RPI_BRANCH ?= "rpi-5.15.12-rt"

SRCREV = "185311cc85b1823d747e866d4f0157ec227cda62"

require linux-raspberrypi_5.15.inc
