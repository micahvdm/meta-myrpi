SUMMARY = "Raspberry Pi development image"
LICENSE = "MIT"
# This image provides wifi, ethernet, and usual development tools

require myrpi-image-base.bb

# buildessential installs: gcc, make, autoconf, etc
CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-core-buildessential \
"

# extra dev tools
IMAGE_INSTALL_append = " \
    cmake \
    git \
    perf \
    dtc \
"

# kernel dev tools
#IMAGE_INSTALL_append = " \
#    kernel-dev  \
#    kernel-devsrc \
#    cyclitest \
#    lmbench \
#    stress-ng \
#    sysbench \    
#"

# Extra development features
# source : https://developer.ridgerun.com/wiki/index.php?title=Preparing_Yocto_Development_Environment_for_Debugging
#EXTRA_IMAGE_FEATURES += "\
#      dbg-pkgs \       # adds -dbg packages for all installed packages and symbol information for debugging and profiling.
#      tools-debug \    # adds debugging tools like gdb and strace.
#      tools-profile \  # add profiling tools (oprofile, exmap, lttng valgrind (x86 only))
#      tools-testapps \ # add useful testing tools (ts_print, aplay, arecord etc.)
#      debug-tweaks \   # make image for suitable of development, like setting an empty root password
#      tools-sdk \      # OPTIONAL: adds development tools (gcc, make, pkgconfig, etc)  
#      dev-pkgs"        # OPTIONAL: adds -dev packages for all installed packages

# ATTENTION: these options considerably increase the image size 
# Specifies to build packages with debugging information
#DEBUG_BUILD = "1"
# Do not remove debug symbols
#INHIBIT_PACKAGE_STRIP = "1"
# OPTIONAL: Do not split debug symbols in a separate file
#INHIBIT_PACKAGE_DEBUG_SPLIT= "1"

# set the free space in the image. If not set, it will be minimal, i.e. few hundreds of MBytes
#IMAGE_ROOTFS_EXTRA_SPACE_append = " + 8000000"
