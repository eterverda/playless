Repository layout
=================

Groups list

	index.ls

Distribution line directory

	<group>[/branches/<branch>][/variants/<variant>][/archive]

Brancles list in group directory. (Contains relative paths like `branches/<branch>`)

	<group>/brancles.ls

Variants list in branch or group repository. (Contains relative paths like `variants/<variant>`)

	<group>[/branches/<branch>]/variants.ls

Distribution line index

	<group>[/branches/<branch>][/variants/<variant>][/archive]/index.jsons

Repository marker

	.playless

Corresponding html files

	index.html
	<group>/brancles.html
	<group>[/branches/<branch>]/variants.html
	<group>[/branches/<branch>][/variants/<variant>][/archive]/index.html

Distribution naming
===================

	dist :: <timestamp>[-<build>]-<package>-<selector>[-debug]
	filename :: <dist>.apk
	icon :: <dist>[-<qualifier>].png

- `timestamp` must conform to rfc3339 (preferrable UTC)
- `package` must be valid java package name same as packageName in apk
- `selector` hash based on `minSdkVersion`, `supportScreens`, `compatibleScreens`, `supportsGlTextures`, `usesFeatures`, abis.

Command line reference
======================

	playless publish [-g] [-b] [-v] <file> [-t] [-n] [-f]

	playless archive [-g+|-G] [-b+|-B] [-v+|-V] [-t] [<package>+]

	playless delete [-g+|-G] [-b+|-B] [-v+|-V] [-t] [<package>+]

	playless list [-g+|-G] [-b+|-B] [-v+|-V] [<package>+]

Common arguments

	-g|--group=
	-G|--all-groups
	-b|--branch=
	-B|--all-branches
	-v|--variant=
	-V|--all-variants
	-n|--build=
	-t|--timestamp=
	-f|--force

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
