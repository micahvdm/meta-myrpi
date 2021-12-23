# meta-myrpi
Yocto layer for configuring a Raspberry Pi.

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
git clone -b <yocto-version> https://github.com/amamory-embedded/meta-myrpi.git
bitbake-layers add-layer meta-myrpi
$ bitbake myrpi-image -c populate_sdk
```

It's not mandatory to use `myrpi-image`. This image only adds a [couple of tools](recipes-core/images/myrpi-image.bb) on top of the `core-image-minimal` image. Thus, the `core-image-minimal` image is another great option. Check the [reference images here](https://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#ref-images) for other image options.

I am not sure if it is mandatory to include SDK (i.e. `populate_sdk`) in the image. This needs some additional testing in the future.


## Layer Description


### Custom Image

### Wifi Setup



## Contributions, Patches and Pull Requests

Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.
