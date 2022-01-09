# recipe for applying kernel fragments
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

# format:
# COMPATIBLE_MACHINE = '(qemux86|qemumips)'
COMPATIBLE_MACHINE = "(raspberrypi3-64)"

SRC_URI += "file://config-rt.cfg"

SRCREV_machine = "${SRCREV}"
