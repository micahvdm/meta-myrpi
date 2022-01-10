SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DO NOT provides wifi and ethernet

inherit core-image
#require recipes-core/images/core-image-minimal.bb

# This sets the default machine RPi if no other machine is selected:
MACHINE ?= "raspberrypi3-64"
#Prefix to the resulting deployable tarball name
export IMAGE_BASENAME = "Reference-Minimal-Image"
MACHINE_NAME ?= "${MACHINE}"
IMAGE_NAME = "${MACHINE_NAME}_${IMAGE_BASENAME}"

SSTATE_DIR = "/mnt/yocto/rpi3/shared-sstate-cache" 
DL_DIR = "/mnt/yocto/rpi3/downloads" 
TMPDIR = "/mnt/yocto/rpi3/tmp" 
  
# this is a smarter image format that can save alot of time when burning a SD card.
# use balena etcher or bmaptool for faster results
IMAGE_FSTYPES ?= "wic.bz2 wic wic.bmap"

#CORE_IMAGE_EXTRA_INSTALL += "libgomp libgomp-dev libgomp-staticdev"

PREFERRED_PROVIDER_virtual/kernel = "linux-yocto"

IMAGE_FEATURES += "splash"

IMAGE_INSTALL_append = " vim nano htop"

# enable uart serial terminal
ENABLE_UART = "1"
