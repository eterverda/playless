Publishing targets
==================

Publishing **target** has three dimenstions.

1. **group** - defaults to *package* (aka Android's *packageName*, aka *applicationId*)
2. **branch** - defaults to *none*
3. **variant** - defaults to *none*

> ### Note on variants

> Using variants can be pain when variants are changed during project development lifecycle. Alternatives to variants are:
>
> - Groups
> - Packages
> - Special trearing of debug apks
> - Multiple apk support

### Target specifier

General syntax is following

	:<group>:<branch>:<variant>

Branch and variant parts can be ommited (with corresponding semicolon) which means there is no such dimension. Examples:

- *no selector*  - default group, no branch and variant
- `:` or `::` or `:::` - same as above
- `:com.example.app` - explicit group, no branch and variant, 
- `:com.example.app::` - same as above
- `:com.example.app:master:free` - explicit group, branch and variant
- `::master` - default group, explicit branch, no variant

### Target predicate

Syntax is same as target specifier but group, branch and variant parts can be given as `-` which means *any* or *every*. Examples:

- `:-` - every group, no branch and variant
- `:-::` - same as above
- `:-:-:-` - *not* same as above, every group, every branch and every variant, i.e. everything
- `::master:-` - every variant on branch master in default (not *all*) group

### Target directory

	<group>[/branches/<branch>][/variants/<variant>][/archive]

Repository Marker
=================

	.playless

Command line reference
======================

	playless init
	playless publish [<target>] <file>+ [-t] [-n] [-f]
	playless archive [<target>] [<package>+]
	playless delete [<target>] [<package>+] [-t] [--no-archive]
	playless list [<target>] [<package>+] [--no-archive]
	playless static [--no-clean] [--no-html] [--no-ls] [--no-jsons] [--no-json] [--no-empty]

Every command accepts argument `-r` with repository path.

Distribution naming
===================

	dist :: <timestamp>[-<build>]-<package>-<selector>[-debug]
	filename :: <dist>.apk
	record :: <dist>.playless.json
	icon :: <dist>[-<qualifier>].png

- `timestamp` must conform to rfc3339 (preferrable UTC)
- `package` must be valid java package name same as packageName in apk
- `selector` hash based on `minSdkVersion`, `supportScreens`, `compatibleScreens`, `supportsGlTextures`, `usesFeatures`, abis.

Distribution record
===================

Distribution record must be one-line json. Formatting here only for presentation purposes.

	{
		apk: "<>",
		applicationId: "",
		versionCode:,
		timestamp,
		sha1,
		meta: {
			buildNumber,
			versionName[-<qualifier>],
			label[-<qualifier>],
			icon[-<qualifier>]: "<path>",
			description[-<qualifier>]
		},
		selector: {
			minSdkVersion,
			maxSdkVersion,
			supportScreens: [],
			compatibleScreens: [],
			supportsGlTextures: [],
			usesFeatures: [],
			abis: []
		},
		filter: {
			usesConfigurations: [],
			usesLibraries: []
		}
	}


Static repository hosting
=========================

### Generated files

When executing command `playless static` following listing files are created

	index.ls
	<group>/brancles.ls
	<group>[/branches/<branch>]/variants.ls
	<group>[/branches/<branch>][/variants/<variant>]/archive.ls
	<group>[/branches/<branch>][/variants/<variant>][/archive]/index.ls
	<group>[/branches/<branch>][/variants/<variant>][/archive]/index.json
	<group>[/branches/<branch>][/variants/<variant>][/archive]/<dist>.json

Corresponding html files

	index.html
	<group>/brancles.html
	<group>[/branches/<branch>]/variants.html
	<group>[/branches/<branch>][/variants/<variant>][/archive]/index.html

Google Play similarities and differences
==========================================

### Multiple APK support

- `<supports-gl-texture>`
- `<compatible-screens>` (not recommended)
- `<supports-screens>`
- `<uses-feature>` (`name` and `openGlEsVersion`)
- `<uses-sdk>` (namely `minSdkVersion` and `maxSdkVersion`)
- abi

### Filtering

- `<uses-configuration>`
- `<uses-library>`
