SUMMARY = "Raspberry Pi kernel tweaks - using an external kernel w preempt_rt"
LICENSE = "MIT"

LINUX_VERSION ?= "5.15.12"
LINUX_RPI_BRANCH ?= "rpi-5.15.12-rt"
LINUX_RPI_KMETA_BRANCH ?= "yocto-5.15"

SRCREV_machine = "185311cc85b1823d747e866d4f0157ec227cda62"
SRCREV_meta = "91c0a39cfecc9f88f12308acbb2d5f3e4d836852"

require linux-raspberrypi-rt_5.15.inc
