SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DOES NOT provide wifi and ethernet

inherit core-image

IMAGE_INSTALL_append = " vim nano htop"
