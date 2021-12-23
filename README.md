# meta-myrpi
Yocto layer for configuring a Raspberry Pi, extending existing recipes.

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

We use a docker container with Yocto and VNC installed. Check out the [container manual](https://github.com/amamory-embedded/docker-yocto-vnc) to see it's features and how to install it. The first step once the docker image is install is to start its VNC. All the Linux image creation will be done using VNC.

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
$ bitbake myrpi-image -c populate_sdk
```

It's not mandatory to use `myrpi-image`. This image only adds a [couple of tools](recipes-core/images/myrpi-image.bb) on top of the `core-image-minimal` image. Thus, the `core-image-minimal` image is another great option. Check the [reference images here](https://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#ref-images) for other image options.

I am not sure if it is mandatory to include SDK (i.e. `populate_sdk`) in the image. This needs some additional testing in the future.


## Layer Description


### Custom Image

### Wifi Setup

## Testing the Resulting Image in the RPi3


For actual deployment on a RPI3 processor, we need to change the `MACHINE` variable in `~/rpi/build/conf/local.conf` back to its original, `raspberrypi3` or `raspberrypi3-64`.
For example:

```bash
# This sets the default machine to be qemux86-64 if no other machine is selected:
MACHINE ??= "raspberrypi3"
```

It's also recommended to change the output format of the image by adding the following line of the same file:

```bash
IMAGE_FSTYPES ?= "tar.bz2 ext3 rpi-sdimg"
```

After building the image again (this time, it's quick), you will find the following file:

```bash
~/rpi/build$ find /mnt/yocto/tmp/deploy/images/raspberrypi3/ -name *.rpi-sdimg
/mnt/yocto/tmp/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3-20211221153332.rootfs.rpi-sdimg
/mnt/yocto/tmp/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3.rpi-sdimg
```

Remember that the `/mnt/yocto/tmp` is shared between the docker image and the host, so it's easy to burn the image file into an SD card.

Return to the host computer and run `df` to find out the SD card device (assuming it is `/dev/sdb`) and run:

```bash
$ sudo dd if=/<host mounting point>/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3.rpi-sdimg of=/dev/sdb bs=4M
```

## TO DO

  - wifi-ready image;
  - support for [vnc](https://github.com/bmit-pune/meta-toradex-yocto/blob/master/recipes-graphics/vnc/tightvnc_1.3.10.bb);
  - support for [mender](https://github.com/mendersoftware/meta-mender) for remote updates;
  - install [preempt_rt kernel](https://github.com/kdoren/linux/tree/rpi_5.15.10-rt24);
  - install [ROS2](https://github.com/ros/meta-ros/wiki/OpenEmbedded-Build-Instructions) layers, tested with this [tutorial](https://github.com/vmayoral/diving-meta-ros);
  - install opencv, tf-lite2, and similar ML frameworks.

## Contributions, Patches and Pull Requests

Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.
