SUMMARY = "Raspberry Pi kernel tweaks - using a complete .config file"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
## select this line if you DONT want to apply the preempt_rt patch to v5.4.
#SRC_URI += " file://defconfig_5.4"
## select this line if you want to apply the preempt_rt patch to v5.4.
SRC_URI += " file://defconfig_5.4-rt \
file://patch-5.4.74-rt42.patch \
"

unset KBUILD_DEFCONFIG

do_configure_prepend () {
        cp "${WORKDIR}/defconfig" "${B}/.config"  
}

#SUMMARY = "Raspberry Pi kernel tweaks - using a fragment file"
#LICENSE = "MIT"
#
#FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
## the fragment extension must be .cfg. You can add as many fragment as you want
#SRC_URI += " file://config-rt.cfg"
#
