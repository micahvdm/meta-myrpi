FILESEXTRAPATHS_prepend := "${THISDIR}/linux-raspberrypi:"

SRC_URI = " \
    git://github.com/kdoren/linux.git;branch=${LINUX_RPI_BRANCH} \
    "
SRC_URI_remove = "file://rpi-kernel-misc.cfg"

require linux-raspberrypi.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KERNEL_EXTRA_ARGS_append_rpi = " DTC_FLAGS='-@ -H epapr'"
