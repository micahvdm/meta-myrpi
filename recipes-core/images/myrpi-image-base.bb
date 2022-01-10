SUMMARY = "Raspberry Pi base image"
LICENSE = "MIT"
# This image provides wifi, ethernet, and package managment

#inherit core-image
require recipes-core/images/core-image-base.bb
require myrpi-image-minimal.bb

# wifi works on 'core-image-base' but NOT on 'core-image-minimal'
# https://github.com/agherzan/meta-raspberrypi/issues/576
# https://github.com/agherzan/meta-raspberrypi/issues/841
# https://github.com/agherzan/meta-raspberrypi/issues/31

#
# Extra image configuration defaults
#
# The EXTRA_IMAGE_FEATURES variable allows extra packages to be added to the generated
# images. Some of these options are added to certain image types automatically. The
# variable can contain the following options:
#  "dbg-pkgs"       - add -dbg packages for all installed packages
#                     (adds symbol information for debugging/profiling)
#  "src-pkgs"       - add -src packages for all installed packages
#                     (adds source code for debugging)
#  "dev-pkgs"       - add -dev packages for all installed packages
#                     (useful if you want to develop against libs in the image)
#  "ptest-pkgs"     - add -ptest packages for all ptest-enabled packages
#                     (useful if you want to run the package test suites)
#  "tools-sdk"      - add development tools (gcc, make, pkgconfig etc.)
#  "tools-debug"    - add debugging tools (gdb, strace)
#  "eclipse-debug"  - add Eclipse remote debugging support
#  "tools-profile"  - add profiling tools (oprofile, lttng, valgrind)
#  "tools-testapps" - add useful testing tools (ts_print, aplay, arecord etc.)
#  "debug-tweaks"   - make an image suitable for development
#                     e.g. ssh root access has a blank password
# There are other application targets that can be used here too, see
# meta/classes/image.bbclass and meta/classes/core-image.bbclass for more details.
# We default to enabling the debugging tweaks.
EXTRA_IMAGE_FEATURES ?= "debug-tweaks"

#
# Additional image features
#
# The following is a list of additional classes to use when building images which
# enable extra features. Some available options which can be included in this variable
# are:
#   - 'buildstats' collect build statistics
#   - 'image-mklibs' to reduce shared library files size for an image
#   - 'image-prelink' in order to prelink the filesystem image
# NOTE: if listing mklibs & prelink both, then make sure mklibs is before prelink
# NOTE: mklibs also needs to be explicitly enabled for a given image, see local.conf.extended
USER_CLASSES ?= "buildstats image-mklibs image-prelink"

# Supported values are auto, gnome, xfce, rxvt, screen, konsole (KDE 3.x only), none
# Note: currently, Konsole support only works for KDE 3.x due to the way
# newer Konsole versions behave
#OE_TERMINAL = "auto"
# By default disable interactive patch resolution (tasks will just fail instead):
PATCHRESOLVE = "noop"

# CONF_VERSION is increased each time build/conf/ changes incompatibly and is used to
# track the version of this file when it was generated. This can safely be ignored if
# this doesn't mean anything to you.
CONF_VERSION = "1"

# wifi connectivity features
DISTRO_FEATURES_append = " wifi"
# this packagroup includes:
#"packagegroup-base-wifi" -> "iw"
#"packagegroup-base-wifi" -> "kernel-module-aes-generic" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-aes" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-arc4" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-crypto_algapi" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-cryptomgr" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-ecb" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-ieee80211-crypt-ccmp" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-ieee80211-crypt" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-ieee80211-crypt-tkip" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-ieee80211-crypt-wep" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-michael-mic" [style=dotted]
#"packagegroup-base-wifi" -> "kernel-module-zd1211rw" [style=dotted]
#"packagegroup-base-wifi" -> "wireless-regdb-static"
#"packagegroup-base-wifi" -> "wpa-supplicant"
DISTRO_FEATURES_append = " usbhost"
# this packagroup includes:
#"packagegroup-base-usbhost" -> "kernel-module-ehci-hcd" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-mousedev" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-ohci-hcd" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-scsi-mod" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-sd-mod" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-uhci-hcd" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usbcore" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usbhid" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usbmouse" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usbnet" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usbserial" [style=dotted]
#"packagegroup-base-usbhost" -> "kernel-module-usb-storage" [style=dotted]
#"packagegroup-base-usbhost" -> "usbutils"
DISTRO_FEATURES_append = " usbgadget"
# this packagroup includes:

###########################
# USB gadget
# TODO: USB thetering from Android phone is not working for MACHINE=raspberrypi3_64
###########################
# enable usb thetering from phone
# sources: https://raspiproject.altervista.org/usb-and-bluetooth-tethering-with-raspberry-pi/
# https://developer.toradex.com/knowledge-base/usb-device-mode-linux
# https://peppe8o.com/raspberry-pi-portable-hotspot-with-android-usb-tethering/
# https://learn.adafruit.com/turning-your-raspberry-pi-zero-into-a-usb-gadget/ethernet-gadget
# https://raspberrypi.stackexchange.com/questions/124004/detect-rpi4-as-usb-network-interface-g-ether-dwc2
# https://support.criticallink.com/redmine/projects/arm9-platforms/wiki/Enabling_USB_RNDIS_Support

# not sure if this is required ... i might be polluting it.
#IMAGE_INSTALL_append += " kernel-modules"
#MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-module-dwc2"

# This command adds the following line to config.txt:
#   dtoverlay=dwc2,dr_mode=peripheral
#ENABLE_DWC2_PERIPHERAL = "1"

# g_ether is for the gadget-side 
# enable USB thethering
#KERNEL_MODULE_AUTOLOAD += "dwc2 g_ether usb_f_ecm usb_f_eem"
#MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-module-dwc2 kernel-module-g-ether kernel-module-usb-f-ecm kernel-module-usb-f-eem"

# check the following files to check whether the modifications were applied
# $ cat /mnt/yocto/tmp/deploy/images/raspberrypi3/bootfiles/config.txt | grep -i dwc
# $ cat /mnt/yocto/tmp/deploy/images/raspberrypi3/bootfiles/cmdline.txt 

# source: https://www.xaprb.com/blog/2006/05/23/how-to-use-linuxs-proc-config-feature/
# edit the kernel options CONFIG_PROC_FS, CONFIG_IKCONFIG, and CONFIG_IKCONFIG_PROC
# to generate the file /proc/config.gz

# extra tools
IMAGE_INSTALL_append = " bash \
			 bash-completion \
"
# network extra tools
IMAGE_INSTALL_append = " \
        bridge-utils \
        dhcp-client \
        iptables"

# extra dev tools
IMAGE_INSTALL_append = " \
    vim \
    less \
    ldd \
    file \
    openssh \
    gzip \
    wget \
    coreutils \
"

###########################
# VNC
# TODO: not tested yet
###########################
# VNC has lots of depedency. It's heavy. Be sure you need it.
#IMAGE_INSTALL_append += " x11vnc"

# install the package-management tool (opkg, dnf, apt), depending on the format selected next. the default is rpm.
EXTRA_IMAGE_FEATURES += " package-management " 
# this is the address to the package repository
PACKAGE_FEED_URIS = "http://10.30.3.59:8000" 

# removing unsued packages
# https://stackoverflow.com/questions/28765494/yocto-minimal-image-with-package-management
# check the file for the list of features that can be excluded
# /opt/yocto/dunfell/src/poky/meta/recipes-core/packagegroups/packagegroup-base.bb:
DISTRO_FEATURES_remove = " x11 alsa touchscreen bluetooth opengl wayland apm ext2 nfs phone nfc ppp"
#PACKAGE_EXCLUDE = "bluez3 bluez4"

# blacklinsting sound and video/camera kmods so that they are not removed by the kernel but they are also not loaded automatically.
# This can be easily reversed by deleting the blacklist file under `/etc/modprobe.d/`
#KERNEL_MODULE_PROBECONF += "snd"
#module_conf_snd = "snd \
#    blacklist snd_bcm2835  \
#    blacklist snd_pcm \ 
#    blacklist snd_pcm_dmaengine \
#    blacklist snd_soc_core \
#    blacklist snd_timer"

#KERNEL_MODULE_PROBECONF += "videodev"
#module_conf_videodev = "blacklist mc \
#    blacklist bcm2835_v4l2 \
#    blacklist bcm2835_isp \
#    blacklist v4l2_mem2mem \
#    blacklist videobuf2_common \
#    blacklist videobuf2_dma_contig \
#    blacklist videobuf2_memops \ 
#    blacklist videobuf2_v4l2 \
#    blacklist videobuf2_vmalloc \
#    blacklist videodev"

# set the free space in the image. If not set, it will be minimal, i.e. few hundreds of MBytes
#IMAGE_ROOTFS_EXTRA_SPACE_append = " + 8000000"

# to check the size of the image
# check the directory build/buildhistory/images/raspberrypi3_64/glibc/core-image-base/
# to access the dependency, size of each package, etc
INHERIT += "buildhistory"
BUILDHISTORY_COMMIT = "1"
