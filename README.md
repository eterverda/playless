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

Every command accepts argument `-r` with repository path.

Distribution naming
===================

	dist :: <timestamp>-<package>-<selector>[-debug]
	filename :: <dist>.apk
	icon :: <dist>[-<qualifier>].png

- `timestamp` must conform to rfc3339 (preferable Zulu time)
- `package` must be valid java package name same as packageName in apk
- `selector` hash based on `selector` object in distribution record

Distribution record
===================

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
