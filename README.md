# meta-myrpi
Yocto layer for configuring a Raspberry Pi, extending existing recipes. The main goal will be to create a minimal image suitable for robotics and real-time applications/experiments. Thus, features like ethernet, wifi, serial, i2c, will be included. Features like video decoding, bluetooth, audio interfaces, most users apps, most GUI, etc are initially out. Image processing is also out since I don't think RPi3 can do some serious real-time image processing. To be confirmed in the future ... 

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

Considering the above mentioned docker container, open a terminal (refer to the previous section about opening a container terminal) in the `rpi` directory and run:

```bash
$ cd rpi
$ source /opt/yocto/dunfell/src/poky/oe-init-build-env
$ git clone -b dunfell https://github.com/amamory-embedded/meta-myrpi.git
$ bitbake-layers add-layer meta-myrpi
```

The expected `build/conf/bblayers.conf` is:

```
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /opt/yocto/dunfell/src/poky/meta \
  /opt/yocto/dunfell/src/poky/meta-poky \
  /opt/yocto/dunfell/src/poky/meta-yocto-bsp \
  /home/build/rpi/build/meta-myrpi \
  "
BBLAYERS += " \ 
  /opt/yocto/dunfell/src/meta-raspberrypi \ 
  /opt/yocto/dunfell/src/meta-openembedded/meta-oe  \ 
  /opt/yocto/dunfell/src/meta-openembedded/meta-networking  \ 
  /opt/yocto/dunfell/src/meta-openembedded/meta-python  \ 
  "
```

Next, build one of the `myrpi` Yocto images by running:

```bash
$ cd ~/rpi/build
$ bitbake <image name>
```

The following table describes the available  `myrpi` Yocto images. The image size is related related to the size of the `.wic.bz2` file.

| Image                                                              | Size            | Access                                        | Description                                                                                              |
|--------------------------------------------------------------------|-----------------|-----------------------------------------------|----------------------------------------------------------------------------------------------------------|
| [myrpi-image-minimal](.recipes-core/images/myrpi-image-minimal.bb) |  1581 pkgs/59MB | Serial interface or HDMI + keyboard           | Includes `core-image` definitions plus htop, vim, nano. It enables RPi serial port.                      |
| [myrpi-image-base](.recipes-core/images/myrpi-image-base.bb)       |  1715 pkgs/67MB | All previous plus SSH via ethernet and wifi.  | Includes all the definitions of the previous image plus wifi, ethernet, and opkg for package management. |
| [myrpi-image-dev](.recipes-core/images/myrpi-image-dev.bb)         | 1805 pkgs/125MB | The same as the previous.                     | Includes all the definitions of the previous image plus development tools.                               |

According to this [meta-raspberry issue](https://github.com/agherzan/meta-raspberrypi/issues/576), wifi dont work with `core-image-minimal` image. So we choose `core-image-base` and removed the resources that wont be used (e.g. video decoding, audio, bluetooth, etc). Still, there are space to minimization like, for example, removing the kernel modules of unsused resources. Check the [reference images here](https://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#ref-images) for other image options.

## Build the SDK

If you want to build the toolchain for the target `MACHINE`, run: 

```
$ bitbake <image> -c populate_sdk
```

## Supported Kernel Versions

Currently, this layer supports two kernel versions supported by meta-raspberrypi:

 - v5.4;
 - v4.19-rt, with PREEMPT_RT patch, enabling the kernel option `CONFIG_PREEMPT_RT_FULL`.

To switch the kernel versions, go to [`conf/layer.conf`](./conf/layer.conf) and choose one of these options:

 - `PREFERRED_PROVIDER_virtual/kernel = "linux-raspberrypi-rt"`
 - `PREFERRED_PROVIDER_virtual/kernel = "linux-raspberrypi"`

## Kernel Customization

If required, this is the command to configure/customize the kernel:

```bash
$ bitbake -c menuconfig virtual/kernel
```

One can configure the kernel by manually setting the options using `menuconfig` or do it automatically, like in this layer, as described in `recipes-kernel/linux/linux-raspberrypi/linux-raspberrypi_5.4%.bbappend`. There are two versions of the kernel append script. The default version applies an entire `.config` file to the kernel configuration. Check the [deconfig](./recipes-kernel/linux/linux-raspberrypi/files/defconfig) file. The second version, commented in the same bbappend file, can be used to apply kernel configuration fragments (i.e. a smaller file including only the modified parts) instead of an entire configuration file. Check the [config-rt.cfg](./recipes-kernel/linux/linux-raspberrypi/files/config-rt.cfg) fragment file for an example.

After manually setting the kernel configuration with the `menuconfig` task, run the following command to create a kernel configuration fragment:

```bash
$ bitbake -c diffconfig virtual/kernel
```

The generated fragment file (`fragment.cfg`) can be included into the recipe to configure the kernel automaticaly in the next time. There might be multiple fragments. Check [Customizing the Linux kernel](https://variwiki.com/index.php?title=Yocto_Customizing_the_Linux_kernel) for more information.

When ready, save the entire kernel configuration by running:

```bash
$ bitbake -c savedefconfig virtual/kernel
```

The resulting `defconfig` file contains the entire kernel configuration and, as the fragment file, can be used to reproduce the exact same kernel configuration.

In the kernel source there is a script in `scripts/kconfig/merge_config.sh` to merge configuration fragments into `.config`. The kernel source is located in `/mnt/yocto/rpi3/tmp/work-shared/raspberrypi3-64/kernel-source`. So, by executing this script in the build directory we can update the `.config` with our fragments. This is the general format, assuming you are in the linux kernel source directory:

```bash
$ scripts/kconfig/merge_config.sh -m -O <destination-dir> source.config source-frag.cfg 
```

And this is an example with Yocto:

```bash
$ cd ~/rpi/build
$ /mnt/yocto/rpi3/tmp/work-shared/raspberrypi3-64/kernel-source/scripts/kconfig/merge_config.sh -m -O /mnt/yocto/rpi3/tmp/work-shared/raspberrypi3-64/kernel-source/.kernel-meta/cfg/ /mnt/yocto/rpi3/tmp/work-shared/raspberrypi3-64/kernel-source/.kernel-meta/cfg/.config meta-myrpi/recipes-kernel/linux/linux-raspberrypi/files/config-rt.cfg 
```

The linux kernel source directory also includes a script to compare config files:

```
$ <kernel-source>/scripts/diffconfig .config1 .config2
```

## Debugging the Kernel Configuration

Kernel configuration can be a bit trick and it might not work in the first time. The first thing to check, even before the first kernel compilation, is to see if the kernel bbappend file in this layer is corretly referenced in the Yocto project. Run the following command:

```
$ bitbake-layers show-appends | grep linux
...
linux-raspberrypi_5.4.bb:
  /home/build/rpi/build/meta-myrpi/recipes-kernel/linux/linux-raspberrypi/linux-raspberrypi_5.4%.bbappend
...
```

This output means that the bbappend file is read by Yocto. Next, this is 
suggested workflow for building and debugging the kernel configuration:

```
$ bitbake -c cleansstate virtual/kernel
$ bitbake -f -c compile virtual/kernel
$ bitbake -c deploy virtual/kernel
$ bitbake <image-name>
```

In fact, it is not necessary to build the entire image to check the kernel configuration. 
When the kernel compilation starts, interrupt the process with CTRL + C and grep the following file:

```
$ cat /mnt/yocto/rpi3/tmp/work/raspberrypi3_64-poky-linux/linux-raspberrypi/1_5.4.72+gitAUTOINC+5d52d9eea9_154de7bbd5-r0/linux-raspberrypi3_64-standard-build/.config | grep <option-name>
```

If the option has the expected value, then the configuration merge process went well. 

In Yocto, the recipes execute several tasks. Each task generates log files that can be used for debugging. For instance, the `virtual/kernel` recipe that runs linux-raspberrypi kernel extension, generates logs in the following folder:

```bash
$ ls <base-tmp>/tmp/work/raspberrypi3_64-poky-linux/linux-raspberrypi/1_5.4.72+gitAUTOINC+5d52d9eea9_154de7bbd5-r0/temp/
```

Run this command to check the list of tasks related to the kernel.

```bash
$ bitbake virtual/kernel -c listtasks
```

And check the `log.task_order` file in the same directory. this file shows the execution order of the tasks. Follow this order to check the log and run files.

```bash
$ cat /mnt/yocto/rpi3/tmp/work/raspberrypi3_64-poky-linux/linux-raspberrypi/1_5.4.72+gitAUTOINC+5d52d9eea9_154de7bbd5-r0/temp/log.task_order
do_cleansstate (8237): log.do_cleansstate.8237
do_cleanall (8238): log.do_cleanall.8238
do_fetch (8395): log.do_fetch.8395
do_unpack (8524): log.do_unpack.8524
do_prepare_recipe_sysroot (8525): log.do_prepare_recipe_sysroot.8525
do_symlink_kernsrc (8572): log.do_symlink_kernsrc.8572
do_kernel_checkout (8573): log.do_kernel_checkout.8573
do_validate_branches (9030): log.do_validate_branches.9030
do_kernel_metadata (9055): log.do_kernel_metadata.9055
do_patch (11406): log.do_patch.11406
do_kernel_configme (11511): log.do_kernel_configme.11511
do_deploy_source_date_epoch (11512): log.do_deploy_source_date_epoch.11512
do_configure (11874): log.do_configure.11874
do_menuconfig (12146): log.do_menuconfig.12146
```

This specific kernel `bbappend` extends the following definitions:

```
meta-raspberrypi/recipes-kernel/linux/linux-raspberrypi.inc
poky/meta/classes/kernel.bbclass
```

In more complex situations you might need to put some debugging prints in these two files to figure out the kernel configuration process.

Finally, when running the kernel in the board, it is possible to check whether the kernel options where correctly deployed. For instance, one of the options set in this layer is to put the frequency scaling mode to `performance` by default. In this case, this can be checked with this command:

```bash
$ cat /sys/devices/system/cpu/cpufreq/policy0/scaling_governor 
performance
$ cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor 
performance
performance
performance
performance
```

Another kernel option set in this example is `CONFIG_IKCONFIG`, which writes the kernel configuration file into `/proc/config.gz`. Thus, one can check the option values by running in the board:

```bash
$ zcat /proc/config.gz | grep <option-name>
```

Please refer to the [Yocto Project Linux Kernel Development Manual](https://docs.yoctoproject.org/kernel-dev/index.html) for more information.

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

To check if the `bbappend` files are being applied. Since this layer has a kernel append, it is expected to find an output like this:

```
$ bitbake-layers show-appends 
```

Or run this command to find the existing bbappend files in your setup:

```
$ find -name "linux-*.bbappend" 
```

It's also possible to increase the verbosity level, like this:
```
$ bitbake -D -v <image/recipe name>
```

As mentioned before, in Yocto, the recipes execute several tasks. It is very usefull to debug the task execution by looking at the recipe `temp/` directory, where all the `run.*` and `log.*` files are stored.

## Testing the Resulting Image in the RPi3

For actual deployment on a RPI3 processor, we need to make sure that the `MACHINE` variable in `~/rpi/build/meta-myrpi/conf/layer.conf` is set to `raspberrypi3-64`.
For example:

```bash
# This sets the default machine to be qemux86-64 if no other machine is selected:
MACHINE ??= "raspberrypi3-64"
```

It's also recommended to use `wic` image format. It is a smarter image format that can save alot of time when burning a SD card. Use [balena etcher](https://www.balena.io/etcher/) or [bmaptool](https://github.com/intel/bmap-tools) for faster results.

```bash
IMAGE_FSTYPES ?= "wic.bz2 wic wic.bmap"
```

After building the image again (this time, it's quick), you will find the following file:

```bash
~/rpi/build$ find /mnt/yocto/tmp/deploy/images/raspberrypi3-64/ -name *.wic.bz2
/mnt/yocto/tmp/deploy/images/raspberrypi3-64/myrpi-image-minimal-raspberrypi3-64.wic.bz2
```

Remember that the `/mnt/yocto/tmp` is shared between the docker image and the host, so it's easy to burn the image file into an SD card. Return to the host computer and run `df` to find out the SD card device (assuming it is `/dev/sdb`) and run `bmaptool`. If `bmaptool` is not installed, run: 

```bash
$ sudo apt install bmap-tools
```
Then, insert the SD card, unmount the its partitions, proceed with the actual copy to the SD card by running:

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

## Ways access to the board

At the time of first boot with the `myrpi-image-base` or `myrpi-image-dev`, there are tree ways to access the board:

 1. [Serial terminal](https://medium.com/@sarala.saraswati/connecting-to-your-raspberry-pi-console-via-the-serial-cable-44d7df95f03e) configured with baud rate of 115200 bps. Recommend the use of screen: `screen /dev/ttyUSB0 115200`;
 2. SSH via ethernet cable. `avahi` service is installed. Thus, the board can be accessed by its `MACHINE` variable value, in this case `raspberrypi3-64.local`;
 3. Using HDMI monitor and keyboard.

After the wifi is configured using the procedure bellow, it becomes the forth access option, i.e. SSH via wifi. The image `myrpi-image-minimal` does not have the second option.

## First Boot configuration

Change the WIFI SSID and password by editing the `/etc/wpa_supplicant.conf` file with the following commands:

```bash
$ wpa_passphrase "MY SSID"
network={
        ssid="MY SSID"
        #psk="MYPASSWD"
        psk=12434887a7b7c994338779d9e998f8a7
}
$ nano /etc/wpa_supplicant.conf
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

# Some data and results of this image

Here are some results from the `core-image-base` based image:

```bash
root@raspberrypi3:~$ uname -a
Linux raspberrypi3-64 5.4.72-v8 1 SMP PREEMPT Mon Oct 19 11:12:20 UTC 2020 aarch64 aarch64 aarch64 GNU/Linux
```

The same for the kernel version 4.19 with PREEMPT_RT patch:

```bash
root@raspberrypi3-64:~$ uname -a
Linux raspberrypi3-64 4.19.71-rt24-v8 1 SMP PREEMPT RT Tue Dec 17 14:50:30 UTC 2019 aarch64 GNU/Linux
```

```bash
root@raspberrypi3:~$ df -h
Filesystem      Size  Used Avail Use% Mounted on
/dev/root       639M  400M  193M  68% /
devtmpfs        328M     0  328M   0% /dev
tmpfs           457M  192K  457M   1% /run
tmpfs           457M   80K  457M   1% /var/volatile
/dev/mmcblk0p1   63M   38M   25M  62% /boot

root@raspberrypi3:~$ free -h
              total        used        free      shared  buff/cache   available
Mem:         935236       44952      852768         272       37516      872468
Swap:             0           0           0

root@raspberrypi3-64:~$ ps | wc -l
103

root@raspberrypi3-64:~$ lsmod
Module                  Size  Used by
ipv6                  548864  22
brcmfmac              282624  0
brcmutil               20480  1 brcmfmac
sha256_generic         16384  0
libsha256              20480  1 sha256_generic
vc4                   278528  0
bcm2835_isp            32768  0
bcm2835_codec          49152  0
bcm2835_v4l2           49152  0
v4l2_mem2mem           36864  1 bcm2835_codec
bcm2835_mmal_vchiq     36864  3 bcm2835_codec,bcm2835_v4l2,bcm2835_isp
videobuf2_vmalloc      20480  1 bcm2835_v4l2
videobuf2_dma_contig    20480  2 bcm2835_codec,bcm2835_isp
cfg80211              815104  1 brcmfmac
videobuf2_memops       16384  2 videobuf2_vmalloc,videobuf2_dma_contig
videobuf2_v4l2         32768  4 bcm2835_codec,bcm2835_v4l2,v4l2_mem2mem,bcm2835_isp
snd_soc_core          225280  1 vc4
snd_bcm2835            32768  0
videobuf2_common       61440  5 bcm2835_codec,videobuf2_v4l2,bcm2835_v4l2,v4l2_mem2mem,bcm2835_isp
snd_pcm_dmaengine      20480  1 snd_soc_core
rfkill                 36864  1 cfg80211
snd_pcm               139264  4 vc4,snd_bcm2835,snd_soc_core,snd_pcm_dmaengine
videodev              307200  6 bcm2835_codec,videobuf2_v4l2,bcm2835_v4l2,videobuf2_common,v4l2_mem2mem,bcm2835_isp
snd_timer              45056  1 snd_pcm
mc                     57344  6 videodev,bcm2835_codec,videobuf2_v4l2,videobuf2_common,v4l2_mem2mem,bcm2835_isp
vc_sm_cma              40960  2 bcm2835_mmal_vchiq,bcm2835_isp
snd                   106496  4 snd_bcm2835,snd_timer,snd_soc_core,snd_pcm
cec                    53248  1 vc4
vchiq                 368640  3 vc_sm_cma,snd_bcm2835,bcm2835_mmal_vchiq
sdhci_iproc            16384  0
uio_pdrv_genirq        16384  0
uio                    24576  1 uio_pdrv_genirq
```

## Changing this Layer

### Use this Layer with Another Raspberry Pi


- `MACHINE ?= "raspberrypi3-64"` in [layer.conf](./conf/layer.conf). Check `meta-raspberrypi/conf/machine/` for the supported RPi models;

### Use this Layer with Another Yocto Version

If you want to use this layer with another Yocto version rather than `dunfell`, you will probably need to change:

 - `LAYERSERIES_COMPAT_meta-myrpi` in [layer.conf](./conf/layer.conf);
 - [bbappend](./recipes-kernel/linux/linux-raspberrypi/linux-raspberrypi_5.4%.bbappend) file, updating the the corresponding kernel version which might imply modifying the defconfig and the fragments.

## TO DO

New features for the future:

  - [ ] support for OAT
    - [ ] [mender](https://github.com/mendersoftware/meta-mender) for remote updates;
    - [ ] [meta-updater](https://docs.ota.here.com/ota-client/latest/add-ota-functonality-existing-yocto-project.html)
      - [ ] https://docs.ota.here.com/ota-client/latest/build-raspberry.html
      - [ ] https://github.com/advancedtelematic/meta-updater-raspberrypi
      - [ ] https://github.com/advancedtelematic/meta-updater
  - [x] install preempt_rt kernel (v4.19) from meta-raspberrypi;
  - [ ] install [preempt_rt kernel](https://github.com/kdoren/linux/tree/rpi_5.15.10-rt24)(v5.15);
  - [ ] install [ROS2](https://github.com/ros/meta-ros/wiki/OpenEmbedded-Build-Instructions) layers, tested with this [tutorial](https://github.com/vmayoral/diving-meta-ros);
  - [ ] setup the cross debugger;
  - [ ] implement [testing](https://docs.yoctoproject.org/test-manual/intro.html#) and integrate with a [buildbot CI framework](https://git.yoctoproject.org/yocto-autobuilder2/tree/README.md);

## References

 - [A practical guide to BitBake](https://a4z.gitlab.io/docs/BitBake/guide.html)
 - [bitbake commands](https://backstreetcoder.com/bitbake-commands/)
 - [Wind River Linux - Platform Development Guide](https://docs.windriver.com/bundle/Wind_River_Linux_Platform_Developers_Guide_LTS_19/) excelent documentation on how to use Yocto for their Linux product;
 - Applying the [PREEMPT_RT patch to rpi](https://github.com/kdoren/linux/wiki/Building-PREEMPT_RT-kernel-for-Raspberry-Pi);
 - [HOWTO setup Linux with PREEMPT_RT properly](https://wiki.linuxfoundation.org/realtime/documentation/howto/applications/preemptrt_setup);
 - [LeMaRiva](https://lemariva.com/blog/category/rt-systems) series of posts of [RT kernels](https://github.com/lemariva/RT-Tools-RPi) on RPi boards;
  

## Contributions, Patches and Pull Requests

Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.

## Authors

 - Alexandre Amory (December 2021), ReTiS Lab, Scuola Sant'Anna, Pisa, Italy.
