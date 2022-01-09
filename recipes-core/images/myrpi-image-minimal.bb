SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DO NOT provides wifi and ethernet

require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL_append = " vim nano htop"

# enable uart serial terminal
ENABLE_UART = "1"
