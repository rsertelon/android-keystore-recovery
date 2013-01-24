## Android Keystore Recovery

This project aims to solve the "password forgotten" problem for (Android) developers who happen to manage java keystore(s).

## Features

This bruteforce tool is very simple, yet efficient. It will try all password combinations matching `[A-Za-z0-9]+`, from the shortest password, up to the solution.

There are currently three options:

* `-l <minLength>` sets a minimum length for the password, if you know that it should have at least n characters (can save a lot of time)
* `-f <fromPsswd>` sets the combination from which the brute force should start
* `-t <toPasswd>` sets the last combination that sould be tested

> Note: If you want to resume a stopped brute force, I suggest that you take the second last tried password that was stored in `$HOME/AndroidKeystoreRecovery.log`. Indeed, as actor computation is asynchronous, there is no guarantee that every password before the last one were _really_ tried by the software.

## Usage

1. Download the [project jar](http://download.bluepyth.fr/releases/akr-1.0.1.jar)
2. Launch the bruteforce search thanks to this command : `java -jar akr-1.0.1.jar <keystore> [opts]`

## Technical Information

This software runs on the Java Virtual Machine (JVM), so you need to have a Java Runtime Environnement (JRE) installed on your computer. If you don't have one, get it at http://java.com.

To benefit from multi-core computers, this software uses the awesome [Akka](http://akka.io) actor library.

## Development

* __Pull Requests__: I accept every meaningful pull request that you might offer. Please add description and comments in the code :)
* __Issues__: You can create issues, however, I don't know when I'll have time to fix them (will do my best!)

## Licence

Copyright Romain Sertelon 2013

This software is licenced under the GNU Public Licence v3 (GPLv3), you can find it in the `LICENCE` file.
