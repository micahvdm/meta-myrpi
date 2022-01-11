FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
#814dc146f5629976801f31c59d967c79
#SRC_URI += " file://defconfig;md5=814dc146f5629976801f31c59d967c79"
SRC_URI += " file://defconfig"

unset KBUILD_DEFCONFIG

do_configure_prepend () {
        cp "${WORKDIR}/defconfig" "${B}/.config"  
}

#inherit kernel

#addtask copy_defconfig after do_kernel_configcheck before do_compile

#KERNEL_DEFCONFIG = "/home/build/myrpi/build/meta-myrpi/recipes-kernel/linux/linux-raspberrypi/files/defconfig"

#do_copy_defconfig () {
#    cp ${WORKDIR}/defconfig ${B}/.config
#}

#FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
#SRC_URI += "file://config-rt.cfg"

#do_configure_append() {
#    ${S}/scripts/kconfig/merge_config.sh -m -O ${B} ${B}/.config ${WORKDIR}/config-rt.cfg
#}

# recipe for applying kernel fragments
#FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

#inherit kernel
#require recipes-kernel/linux/linux-yocto.inc

# format:
# COMPATIBLE_MACHINE = '(qemux86|qemumips)'
#COMPATIBLE_MACHINE = "(raspberrypi3-64)"

#SRC_URI += "file://config-rt.cfg"

#SRCREV_machine = "${SRCREV}"
