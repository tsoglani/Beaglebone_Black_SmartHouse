
inside the code you might have to modify:


-DeviceID : in case you want to use more than one beaglebone (or any) devices in the same local network, each beaglebone device must have a unique DeviceID. 
Example: if we have 4 beaglebone devices connected to local network, each one MUST have a unique ID :
         the first Ruspberry device DeviceID will be 0, the second device's DeviceID will be 1
         the third will be 2 the fourth will be 3 ...    (this is important if you want to open each output seperately)
-int NumberOfBindingCommands : the number of commands you want to bind with one or more outputs on initializePowerCommans() function.
- initializePowerCommans() function (most important): in this function you activate addCommandsAndPorts function;

**addCommandsAndPorts function has 3 parameters: 
the first parameter is an id (inside the code it is "i" and used in switch cases, so it changes automatically) you don't have to change it, except if your Relay has more that NumberOfBindingCommands outputs.

**the second parameter is an array of a string-text, the first text in this array is sent to the client's device and is used for switching the buttons from the client's device. When you switch the button, the command is sent to beaglebone server. One or more commands are bound to one or more beaglebone outputs (see the third parameter paragraph below). This way you can set one or more beaglebone outputs on and off at once, with one command. You have the option to put more than one commands in case you want to activate or deactivate the outputs with speech commands (you can also do it with speech command, by saying the command and then "on" or "off" word).

** the third parameter is an array of integers(one  or more), each integer represents one output in the beaglebone device. The width of outputs in beaglebone device is from 0 to 20, you can put one or more integers to activate or deactivate one or more output with one (or more) command (see parameter two).
