SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DO NOT provides wifi and ethernet

inherit core-image

IMAGE_INSTALL_append = " vim nano htop"
