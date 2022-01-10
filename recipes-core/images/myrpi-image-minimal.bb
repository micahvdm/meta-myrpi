SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DO NOT provides wifi and ethernet

inherit core-image
#require recipes-core/images/core-image-minimal.bb

#CORE_IMAGE_EXTRA_INSTALL += "libgomp libgomp-dev libgomp-staticdev"

PREFERRED_PROVIDER_virtual/kernel = "linux-yocto"

IMAGE_FEATURES += "splash"

IMAGE_INSTALL_append = " vim nano htop"

# enable uart serial terminal
ENABLE_UART = "1"
