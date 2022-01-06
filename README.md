# meta-myrpi
Yocto layer for configuring a Raspberry Pi, extending existing recipes. This is a work in progress with lot's of road ahead. The main goal will be to create a minimal image suitable for robotics and real-time applications/experiments. Thus, features like ethernet, wifi, serial, i2c, will be included. Features like video decoding, bluetooth, audio interfaces, most users apps, most GUI, etc are initially out. Image processing is also out since I don't think RPi3 can do some serious real-time image processing. To be confirmed in the future ... 

If you want to build a recipe with your own software, please refer to [`learning-yocto`](https://github.com/amamory-embedded/learning-yocto).

## Dependencies

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  * branch: master
  * revision: HEAD

* URI: git://git.openembedded.org/meta-openembedded
  * layers: meta-oe, meta-networking, meta-python
  * branch: master
  * revision: HEAD

* URI: git://github.com/agherzan/meta-raspberrypi/
  * branch: master
  * revision: HEAD

## Yocto Instalation

We use a docker container with Yocto and VNC installed. Check out the [container manual](https://github.com/amamory-embedded/docker-yocto-vnc) to see it's features and how to install it. The first step once the docker image is install is to start its VNC. All the Linux image creation can be done using VNC or a terminal within the docker container.

## Adding the meta-myrpi layer to your build

On your existing Yocto project run:

```bash
cd <yocto-proj>
source <poky-src>/oe-init-build-env
git clone -b <yocto-version> https://github.com/amamory-embedded/meta-myrpi.git
bitbake-layers add-layer meta-myrpi
```

Considering the above mentioned docker container, within VNC, open a terminal and run:

```bash
cd ~/rpi
source /opt/yocto/dunfell/src/poky/oe-init-build-env
git clone -b dunfell https://github.com/amamory-embedded/meta-myrpi.git
bitbake-layers add-layer meta-myrpi
bitbake core-image-base -c populate_sdk
bitbake core-image-base
```
According to this [meta-raspberry issue](https://github.com/agherzan/meta-raspberrypi/issues/576), wifi dont work with `core-image-minimal` image. So we choose `core-image-base` image but I am not happy because it adds lot's other resources I wont use (e.g. video decoding, audio, bluetooth, etc). I still have to test other images to find out one with minimal footprint. Check the [reference images here](https://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#ref-images) for other image options.

I am not sure if it is mandatory to include SDK (i.e. `populate_sdk`) in the image. This needs some additional testing in the future. The last command `bitbake core-image-base` will create the packages and the image.

## Kernel Customization

If required, this is the command to configure/customize the kernel:

```bash
$ bitbake -c menuconfig virtual/kernel
```

at the end, it will create a `.config` file with the kernel parameters. It can also create a configuration fragment (i.e. a smaller file including only the modified parts) by running the followinf command:

```bash
bitbake -c diffconfig virtual/kernel
```

This fragment file can be embedded into a recipe to configure the kernel automaticaly in the next time. Check [Customizing the Linux kernel](https://variwiki.com/index.php?title=Yocto_Customizing_the_Linux_kernel) for more information.

## Layer Description

This layer adds the following features to the RPi3:

  - [x] wifi-ready image based on `core-image-base`;
  - [x] SSH server;

and thse features are under testing:

  - [ ] VNC ... still thinking if it worth the extra weight in the image;
  - [ ] USB gadget to support thetering;

## Build History

This layer enables the `buildhistory` feature of Yocto. This feature enables to compare the resulting image from different builds, to explore the package depedencies, and to check the size of each package. After a successful build, check the contents in the directory `build/buildhistory/images/raspberrypi3_64/glibc/core-image-base/`. Also, in the build directory, it is possible to execute the following script to compare the two latest builds.

```
$ buildhistory-diff
```

## Debugging Build Errors

When dealing with build errors it's useful to check the value of bitbake variables using a command like this one:

```
$ bitbake -e <recipe name> | grep ^<variable name>=
```

## Testing the Resulting Image in the RPi3

For actual deployment on a RPI3 processor, we need to change the `MACHINE` variable in `~/rpi/build/conf/local.conf` back to its original, `raspberrypi3` or `raspberrypi3-64`.
For example:

```bash
# This sets the default machine to be qemux86-64 if no other machine is selected:
MACHINE ??= "raspberrypi3"
```

It's also recommended to change the output format of the image by adding the following line of the same file. `wic` is a smarter image format that can save alot of time when burning a SD card. Use [balena etcher](https://www.balena.io/etcher/) or [bmaptool](https://github.com/intel/bmap-tools) for faster results.

```bash
IMAGE_FSTYPES ?= "wic.bz2 wic wic.bmap"
```

After building the image again (this time, it's quick), you will find the following file:

```bash
~/rpi/build$ find /mnt/yocto/tmp/deploy/images/raspberrypi3/ -name *.rpi-sdimg
/mnt/yocto/tmp/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3-20211221153332.rootfs.rpi-sdimg
/mnt/yocto/tmp/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3.rpi-sdimg
```

The resulting image in `sdimg` format has about **208 MBytes**. The file `/ssd/work/yocto/rpi3-data/tmp/deploy/images/raspberrypi3/core-image-base-raspberrypi3.rootfs.manifest` list all the packages installed in the image. This image has 1889 packages. When building the same layer on top of `core-image-minimal`, its size becomes about 93 MBytes and 220 packages. That's why I want to avoid using `core-image-base`.

Remember that the `/mnt/yocto/tmp` is shared between the docker image and the host, so it's easy to burn the image file into an SD card.

Return to the host computer and run `df` to find out the SD card device (assuming it is `/dev/sdb`) and run `bmaptool`. If `bmaptool` is not installed, run: 

```bash
$ sudo apt install bmap-tools
```
Then, insert the SD card, unmount the its partitions, proceed with the actual copy to the SD card:

```bash
$ sudo bmaptool copy --bmap image.wic.bmap image.wic.bz2 /dev/sdb
bmaptool: info: block map format version 2.0
bmaptool: info: 93287 blocks of size 4096 (364.4 MiB), mapped 50084 blocks (195.6 MiB or 53.7%)
bmaptool: info: copying image 'image.wic.bz2' to block device '/dev/sdb' using bmap file 'image.wic.bmap'
bmaptool: WARNING: failed to enable I/O optimization, expect suboptimal speed (reason: cannot switch to the 'noop' I/O scheduler: [Errno 22] Invalid argument)
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdb'
bmaptool: info: copying time: 11.9s, copying speed 16.4 MiB/sec
```

## First Boot configuration


Using a serial terminal or HDMI monitor + keyboard, change the WIFI SSID and password by editing the following file:

```bash
$ wpa_passphrase "MY SSID" MYPASSWD
network={
        ssid="MY SSID"
        #psk="MYPASSWD"
        psk=12434887a7b7c994338779d9e998f8a7
}
$ nano /etc/wpa_supplicant.conf
$ ifdown wlan0
$ ifup wlan0
```

You might also want to change the static IP at:

```bash
$ nano /etc/network/interfaces
```

Restart wlan0 and it should be ready for SSH.

```bash
$ ifdown wlan0
$ ifup wlan0
```

Here are some results from the `core-image-base` based image:

```bash
root@raspberrypi3:~# uname -a
Linux raspberrypi3 5.4.72-v7 SMP Mon Oct 19 11:12:20 UTC 2020 armv7l GNU/Linux

root@raspberrypi3:~# df -h
Filesystem                Size      Used Available Use% Mounted on
/dev/root               149.7M    121.4M     20.1M  86% /
devtmpfs                333.7M         0    333.7M   0% /dev
tmpfs                   462.2M    152.0K    462.1M   0% /run
tmpfs                   462.2M     76.0K    462.1M   0% /var/volatile

root@raspberrypi3:~# free -h
              total        used        free      shared  buff/cache   available
Mem:         946604       33000      881592         228       32012      894608
Swap:             0           0           0
```


## TO DO

New features for the future:

  - [ ] wifi-ready image based on `core-image-minimal`;
  - [ ] extended custom Yocto images inheriting from  `core-image-minimal` and `core-image-base`;
  - [ ] support for OAT
    - [ ] [mender](https://github.com/mendersoftware/meta-mender) for remote updates;
    - [ ] [meta-updater](https://docs.ota.here.com/ota-client/latest/add-ota-functonality-existing-yocto-project.html)
      - [ ] https://docs.ota.here.com/ota-client/latest/build-raspberry.html
      - [ ] https://github.com/advancedtelematic/meta-updater-raspberrypi
      - [ ] https://github.com/advancedtelematic/meta-updater
  - [ ] install [preempt_rt kernel](https://github.com/kdoren/linux/tree/rpi_5.15.10-rt24);
  - [ ] install [ROS2](https://github.com/ros/meta-ros/wiki/OpenEmbedded-Build-Instructions) layers, tested with this [tutorial](https://github.com/vmayoral/diving-meta-ros);

## References

 - [A practical guide to BitBake](https://a4z.gitlab.io/docs/BitBake/guide.html)
 - 

## Contributions, Patches and Pull Requests

Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.
