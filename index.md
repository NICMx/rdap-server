---
layout: index
title: Home
---

# NEWS

## 2016-09-19

Jool 3.4.5 was released.

1. Added support for [kernels 4.6 and 4.7](https://github.com/NICMx/Jool/issues/219).
2. Deleted constant warning due to an empty pool6.
3. [Improved](https://github.com/NICMx/Jool/issues/223) the implicit blacklist:
	- Blacklisted directed broadcast.
	- Applied the implicit blacklist to EAMT-based translation.  
	  (Among other things, this prevents an overly-intrusive EAMT from hogging packets intended for the translator.)
4. `jool` and `jool_siit` can now be modprobed in the same namespace [without suffering a Netlink socket collision](https://github.com/NICMx/Jool/issues/224).

## 2016-09-19

Jool 3.4.5 was released.

1. Added support for [kernels 4.6 and 4.7](https://github.com/NICMx/Jool/issues/219).
2. Deleted constant warning due to an empty pool6.
3. [Improved](https://github.com/NICMx/Jool/issues/223) the implicit blacklist:
	- Blacklisted directed broadcast.
	- Applied the implicit blacklist to EAMT-based translation.  
	  (Among other things, this prevents an overly-intrusive EAMT from hogging packets intended for the translator.)
4. `jool` and `jool_siit` can now be modprobed in the same namespace [without suffering a Netlink socket collision](https://github.com/NICMx/Jool/issues/224).

