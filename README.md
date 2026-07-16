Beats, Advanced Rhythm Game
=====

Popular, open source rhythm game for Android.
This is the latest snapshot of the SVN code base (r18), migrated to GitHub.
This is the final release of Beats and the subsequent open sourcing of the code. The decision was made to not open source Beats until now due to the extremely messy and disorganized nature of the code (just about every feature since Beats 1.0a is an ugly hack). While Beats is now under a modified BSD license, please do NOT fork the original source code, for sanity reasons. The source code should be used for reference purposes only.
See http://beatsportable.com for more info

All source code is available under Modified BSD license.

~Keripo

---

## About this fork

This fork ([stock-ds/Beats](https://github.com/stock-ds/Beats)) is an **AI-assisted version bump** to get Beats installing and running on Android systems as of 2026. The original 2013-era build (targeting Android 4.4 and using tooling that no longer exists) can't be installed on current devices at all. This isn't a feature update or a rewrite — it's the minimum needed to keep the app runnable:

- Migrated the build from Eclipse ADT/Ant to Gradle, targeting Android 14 (API 34)
- Fixed a crash-on-launch caused by a `Canvas` clipping API removed in modern Android
- Fixed a multi-second UI freeze when exiting a song (blocking `MediaPlayer` teardown on the main thread)
- Dropped a bundled native library that only shipped for obsolete CPU architectures, which was blocking installs on current (arm64) phones
- Moved the Songs/Backgrounds/NoteSkins folders to public storage (`/storage/emulated/0/Beats`) so they're visible in a normal file manager, instead of a hidden per-app folder
- Updated the in-app "Download Songs" link, which pointed to a now-defunct site

None of this changes gameplay or features. If you just want to play, grab the APK from [Releases](../../releases) instead of building it yourself.

In respect of the original author's note above: this exists only to keep an otherwise-abandoned, install-blocked app usable on modern phones. The upstream repository ([Keripo/Beats](https://github.com/Keripo/Beats)) remains the canonical source for reference.

## Building locally

Requirements:
- JDK 17+
- Android SDK with platform `android-34` and build-tools `34.0.0` (install via Android Studio's SDK Manager, or `sdkmanager --sdk_root=<path> "platform-tools" "platforms;android-34" "build-tools;34.0.0"`)

Steps:
1. Clone this repo.
2. Point Gradle at your SDK, either by setting the `ANDROID_HOME` environment variable, or by creating a `local.properties` file in the repo root containing:
   ```
   sdk.dir=/path/to/your/android-sdk
   ```
3. Build the debug APK:
   ```
   ./gradlew assembleDebug
   ```
   (use `gradlew.bat` on Windows)
4. The APK is written to `build/outputs/apk/debug/Beats-debug.apk`. Install it with:
   ```
   adb install -r build/outputs/apk/debug/Beats-debug.apk
   ```

Windows users can instead run [`setup_sdk.ps1`](setup_sdk.ps1), which downloads the Android command-line tools, Gradle, and the required SDK packages automatically (edit the paths at the top of the script first if you don't want the defaults under `c:\_dev`).

This produces a debug-signed APK, which installs and plays fine but isn't eligible for Play Store distribution (that would need a release signing key, which isn't set up in this fork).
