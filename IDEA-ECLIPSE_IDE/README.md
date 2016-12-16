# Beaglebone_SmartHouseServer

[beaglebone debian image](https://debian.beagleboard.org/images/bone-debian-8.4-lxqt-4gb-armhf-2016-05-13-4gb.img.xz) .

Inside this folder there is a [Idea ide](https://www.jetbrains.com/idea/download) java application.

Open it with idea or eclipse ide, and  [refactor the parts you need](https://github.com/tsoglani/Beaglebone_Black_SmartHouse/blob/master/IDEA-ECLIPSE_IDE/modify_.md) .

After you modify it. create a jar file and pass it to beaglebone device (on idea --> build->build Artifacts ).

on java code contentPath="/home/debian/Desktop/resources/";
so you have to copy  Beaglebone_Black_SmartHouse/IDEA-ECLIPSE_IDE/in_and_out/Beaglebone_Black_SmartHouse/src/resources folder and past it to beaglebone desktop.

on Beaglebone device you have to do some  [command line updates](https://github.com/tsoglani/Beaglebone_Black_SmartHouse/blob/master/IDEA-ECLIPSE_IDE/cmd_updates.md) 

 
