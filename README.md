# BluPS2 1.5 Alpha

BluPS2 is an experimental PlayStation 2 emulator project for ARM64 Android devices. It combines a custom handheld interface with the open-source Play! PS2 emulation core.

Current release: BluPS2 1.5 Alpha

Release tag: `v1.5-alpha`

## Download

Download the latest APK from GitHub Releases:

`BluPS2-1.5-Alpha-arm64.apk`

A matching SHA-256 file is published beside the APK for update verification.

## What is new in 1.5 Alpha

- Complete new BluPS2 library interface based on the approved 1.5 design
- BluBox-style left navigation panel
- Light game-library content area with dark blue side navigation
- Library button
- Boot BIOS button
- RetroAchievements button
- Settings button
- Setup / Change Folders button
- About button
- Refresh button
- Add Game button
- Clickable Grid View / List View toggle
- Clickable More menu
- Clickable profile shortcut
- Clickable handheld battery/device panel
- Horizontal PS2 cover-style library cards
- Add Game, Recent, Favorites, Homebrew and Library Scan cards
- Bottom controller-style navigation hints
- Internal storage and microSD folder picker
- Game files stay in their original storage location
- Built-in GitHub release updater
- Automatic update checks with a 15-minute cooldown
- Manual update check from App Settings
- SHA-256 verification where supplied
- Android Package Installer handles final installation confirmation

## Main features

- PS2 emulation powered by the Play! core
- ARM64 Android build
- BluPS2 game library
- Profiles
- Performance Hub
- Controller settings and vibration testing
- 30 FPS and 60 FPS target presets
- Battery information
- Device temperature information where Android exposes a sensor
- Android thermal-status information
- Landscape handheld interface

## Adding games

Open Library and select Add Game to choose a supported PS2 game file.

Use Setup / Change Folders to select a folder from internal storage or microSD through Android's storage picker.

Large game images stay in their original storage location. BluPS2 does not copy them into private app storage.

Use PS2 game files and homebrew you are legally entitled to use. Compatibility depends on the Play! emulation core.

## Built-in updates

BluPS2 checks the official GitHub Releases feed after launch. Automatic checks use a 15-minute cooldown.

Open Settings > App > Check for updates to run a manual check.

When a newer BluPS2 release is available, BluPS2 offers Update now, What's new and Later.

Where a matching SHA-256 file exists, BluPS2 verifies the downloaded APK before opening Android Package Installer.

## Installation

1. Open the latest BluPS2 release on GitHub.
2. Download `BluPS2-1.5-Alpha-arm64.apk`.
3. Install the APK on an ARM64 Android device.
4. Open BluPS2.
5. Add your own legally dumped PS2 games through Library.

## Alpha status

BluPS2 1.5 Alpha is early testing software. Game compatibility, graphics, audio, controls and performance vary between games and devices.

A 60 FPS target does not guarantee a game runs at 60 FPS. Performance depends on the game, Android device and emulation-core compatibility.

## Known limitations

- Some PS2 games fail to boot
- Some games have graphics or audio problems
- Controller behaviour varies between devices
- Device temperature information depends on Android sensor access
- Automatic cover artwork and metadata are still being developed
- RetroAchievements account and game integration still depends on core support

## Legal

BluPS2 does not include PS2 games, copyrighted game files or Sony system software. Use only files you are legally entitled to use.

Do not upload copyrighted PS2 game files to GitHub Issues.

## Credits and licence

BluPS2 uses the open-source Play! PS2 emulator core by jpd002 and contributors under the BSD 2-Clause licence.

BluPS2 branding, Android interface additions, performance tools, library additions and updater integration are maintained separately from the upstream Play! project.

## Bug reports

Use GitHub Issues for reproducible problems. Include your Android device, Android version, BluPS2 version, game title, what happened and steps to reproduce the problem.

## Current version

BluPS2 1.5 Alpha

Tag: `v1.5-alpha`

APK: `BluPS2-1.5-Alpha-arm64.apk`
