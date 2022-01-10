SUMMARY = "Raspberry Pi minimal image"
LICENSE = "MIT"
# This image DO NOT provides wifi and ethernet

inherit core-image

IMAGE_INSTALL_append = " vim nano htop"

# enable uart serial terminal
ENABLE_UART = "1"

# to check the size of the image
# check the directory build/buildhistory/images/raspberrypi3_64/glibc/core-image-base/
# to access the dependency, size of each package, etc
INHERIT += "buildhistory"
BUILDHISTORY_COMMIT = "1"