[![Build Status](https://travis-ci.org/rsertelon/android-keystore-recovery.svg?branch=master)](https://travis-ci.org/rsertelon/android-keystore-recovery)

> Note that this README is updated for the latest snapshot version. If you want to use the last release, please visit [this page](http://bluepyth.github.io/android-keystore-recovery)

## Android Keystore Recovery

This project aims to solve the "password forgotten" problem for (Android) developers who happen to manage java keystore(s).

## Usage

This bruteforce tool is very simple, yet efficient. It will try all password combinations matching `[A-Za-z0-9]+` by default, from the shortest password, up to the solution.

1. Download the [project jar](http://repository.bluepyth.fr/nexus/content/repositories/releases/fr/bluepyth/android-keystore-recovery/1.2.0/android-keystore-recovery-1.2.0-bundle.jar)
2. Launch the bruteforce: `java -jar android-keystore-recovery-1.2.0-bundle.jar <keystore> [opts]`

## Options

These are the available options for AKR:

### Generic options

* `-l <length>   | --min-length <length>` start at given length
* `-f <password> | --from <password>` start at given password (in dictionary attack this is a start line number)
* `-t <password> | --to <password>` stop at given password (in dictionary attack this is a end line number)
* `-pps <number> | --passwords-per-second <number> Will try the given number of passwords per second (Since 1.1)
* `-w <path>     | --wordlist <path> path to wordlist file (example: /../../wordlist.txt)

You can use `--from` and `--to` to parallelize the brute force on several computers.

> Note: If you want to resume a stopped brute force, I suggest that you take the second last tried password that was stored in `$HOME/AndroidKeystoreRecovery.log`. Indeed, as actor computation is asynchronous, there is no guarantee that every password before the last one were _really_ tried by the software.

### Character set options &mdash; since 1.1

* `-lo         | --letters-onlỳ use letters only
* `-no         | --numbers-onlỳ use numbers only
* `-uc         | --upper-casè discards lower-case letters
* `-lc         | --lower-case` discards upper-case letters
* `-ec <chars> | --extra-characters <chars>` add specified characters in combinations

## Time needed to retrieve password

Bruteforce algorithms are not optimized at all, AKR is faster than other bruteforce tools but it will still try __all__ password possibilities.

In our case, we try all the characters like so: `A, B, ..., Z, a, b, ..., z, 0, ..., 9`. This is 62 possibilites for one character of the password.

Depending on your hardware, AKR will try more or less passwords per second, this is a sample calculation with my own computer:

```
- AKR velocity: v = 120 000 passwords/seconds
- Number of combinations for a password of length n: 62^n
- Number of combinations for a 6 characters password: 56 800 235 584
- Time needed to try all these combinations: 56 800 235 584 / v = 5.5 days!
- Time needed for 7 character passwords: 5.5 * 62 = 339 days!
```

As you can see, brute force with a single computer can take a very long time...

You can use the `-f (--from)` and `-t (--to)` options to run AKR on different computers, to parallelize computation and try to shorten the discovery of the password.

## Technical Information

This software runs on the Java Virtual Machine (JVM), so you need to have a Java Runtime Environnement (JRE) installed on your computer. If you don't have one, get it at http://java.com.

To benefit from multi-core computers, this software uses the awesome [Akka](http://akka.io) actor library.

## Development

* __Pull Requests__: I accept every meaningful pull request that you might offer. Please add description and comments in the code :)
* __Issues__: You can create issues, however, I don't know when I'll have time to fix them (will do my best!)

## Licence

Copyright Romain Sertelon 2013

This software is licenced under the GNU Public Licence v3 (GPLv3), you can find it in the `LICENCE` file.
