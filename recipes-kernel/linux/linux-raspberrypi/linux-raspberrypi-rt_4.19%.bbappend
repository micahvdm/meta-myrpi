SUMMARY = "Raspberry Pi PREEMPT_RT kernel  - using a complete .config file"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += " file://defconfig_4.19-rt"

unset KBUILD_DEFCONFIG

do_configure_prepend () {
        cp "${WORKDIR}/defconfig-rt" "${B}/.config"  
}

#SUMMARY = "Raspberry Pi PREEMPT_RT kernel - using a fragment file"
#LICENSE = "MIT"
#
#FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
## the fragment extension must be .cfg. You can add as many fragment as you want
#SRC_URI += " file://config-rt.cfg"
#
