# BluPS2 1.3 Alpha

BluPS2 is an experimental PlayStation 2 emulator project for ARM64 Android devices. It combines a custom blue handheld-focused interface with the open-source Play! PS2 emulation core.

> Current release: **BluPS2 1.3 Alpha**
>
> Release tag: `v1.3-alpha`

## Download

Download the latest APK from the GitHub Releases section:

**BluPS2-1.3-Alpha-arm64.apk**

A matching SHA-256 file is published alongside the APK for update verification.

## What is new in 1.3 Alpha

- Built-in update checker using the official BluPS2 GitHub Releases feed
- Automatic update checks after launch with a 15-minute cooldown
- Manual **Check for updates** option from App Settings
- Update Now, What's New and Later choices when a newer release is available
- SHA-256 verification when a matching checksum file is supplied
- Android Package Installer handles final update confirmation
- New Add Game File option
- New Add Game Folder option
- Scan Library option
- Game files stay in their original internal storage or microSD location
- Updated BluPS2 Library screen
- BluPS2 branding fixes across the Android interface
- Improved handheld layout and spacing
- Reduced UI overlap on smaller screens

## Main features

- PS2 emulation powered by the Play! core
- ARM64 Android build
- BluPS2 blue game-library interface
- Recent and Favorites sections
- Profiles
- Controller settings shortcut
- Controller vibration test
- Performance Hub
- 30 FPS and 60 FPS target presets
- Live battery percentage
- Device temperature display where Android exposes a readable sensor
- Android thermal-status information
- Landscape handheld interface

## Adding games

Open the BluPS2 Library and press **+** or **Add Games**.

Choose a supported PS2 game file or select a folder containing legal backups you own. BluPS2 uses Android's storage picker, so compatible internal storage and microSD locations are available where Android exposes them.

Large game images remain in their original storage location. BluPS2 does not copy them into private app storage.

BluPS2 is intended for your own legally dumped PS2 game files and homebrew. File and game compatibility depends on the Play! emulation core.

## Built-in updates

BluPS2 checks the official GitHub Releases feed after launch. Automatic checks use a 15-minute cooldown.

Open **Settings > App > Check for updates** to run a manual check immediately.

When a genuinely newer BluPS2 release is available, the app offers:

- **Update now** to download the new APK
- **What's new** to read the release notes
- **Later** to keep using the installed version

When a matching SHA-256 file is supplied with the release, BluPS2 verifies the downloaded APK before sending it to Android Package Installer.

## Installation

1. Open the latest BluPS2 release on GitHub.
2. Download `BluPS2-1.3-Alpha-arm64.apk`.
3. Install the APK on an ARM64 Android device.
4. Open BluPS2.
5. Add your own legally dumped PS2 games through the Library.

Android might ask for permission to install apps from your browser or file manager during manual installation.

## Alpha status

BluPS2 1.3 Alpha is early testing software. Game compatibility, graphics, audio, controls and performance vary between games and devices.

A 60 FPS target does not guarantee a game will run at 60 FPS. Actual performance depends on the game, Android device and emulation-core compatibility.

## Known limitations

- Some PS2 games may fail to boot
- Some games may have graphics or audio problems
- Controller behaviour may vary between devices
- Device temperature information depends on sensors exposed by Android
- The interface and settings are still being developed

## Legal

BluPS2 does not include PS2 games, copyrighted game files or Sony system software. Use only game files you are legally entitled to use.

Do not upload copyrighted PS2 game files to GitHub Issues.

## Credits and licence

BluPS2 uses the open-source Play! PS2 emulator core by jpd002 and contributors. Play! is licensed under the BSD 2-Clause licence.

BluPS2 branding, Android interface additions, performance tools, library additions and updater integration are maintained separately from the upstream Play! project.

## Bug reports

Please use GitHub Issues for reproducible problems. Include:

- Android device
- Android version
- BluPS2 version
- Game title
- What happened
- Steps to reproduce the problem

## Current version

**BluPS2 1.3 Alpha**

Tag: `v1.3-alpha`

APK: `BluPS2-1.3-Alpha-arm64.apk`
