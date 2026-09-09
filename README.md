PocketArrows
=====
A StepMania-compatible rhythm game for Android, forked from Beats.

Popular, open source rhythm game for Android.
This is the latest snapshot of the SVN code base (r18), migrated to GitHub.
This is the final release of Beats and the subsequent open sourcing of the code. The decision was made to not open source Beats until now due to the extremely messy and disorganized nature of the code (just about every feature since Beats 1.0a is an ugly hack). While Beats is now under a modified BSD license, please do NOT fork the original source code, for sanity reasons. The source code should be used for reference purposes only.
See http://beatsportable.com for more info

All source code is available under Modified BSD license.

~Keripo

---

## About this fork

This fork ([stock-ds/Beats](https://github.com/stock-ds/Beats)) is now **PocketArrows**: a StepMania-compatible continuation of Beats for current Android, without adopting RevoluTap's UI rewrite.

- Migrated the build from Eclipse ADT/Ant to Gradle, targeting Android 14 (API 34)
- Branding: PocketArrows logo, splash, and intro video
- Play Store identity: `com.stockds.pocketarrows` (Java packages remain `com.beatsportable.beats`)
- Song speed multiplier and free-form scroll/song speed numbers
- Song-select banners, note counts, NPS, and best score
- Hardware SurfaceView so gameplay can match display refresh
- Multitouch drag updates the held column
- On-screen BACK control (needed on emulators without a Back key)
- Songs/Backgrounds/NoteSkins live in public storage (`/storage/emulated/0/Beats`)

If you just want to play, grab the APK from [Releases](../../releases) instead of building it yourself.

In respect of the original author's note above: the upstream repository ([Keripo/Beats](https://github.com/Keripo/Beats)) remains the canonical source for reference.

## Building locally

Requirements:
- JDK 25 to run Gradle (Android Studio's bundled JBR is fine). The app still compiles to Java 8 bytecode for `minSdk 21`.
- Android SDK with platform `android-34` and build-tools `36.0.0` (install via Android Studio's SDK Manager, or `sdkmanager --sdk_root=<path> "platform-tools" "platforms;android-34" "build-tools;36.0.0"`)

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
4. The debug APK is written to `build/outputs/apk/debug/PocketArrows-debug.apk`. Install it with:
   ```
   adb install -r build/outputs/apk/debug/PocketArrows-debug.apk
   ```

Windows users can instead run [`setup_sdk.ps1`](setup_sdk.ps1), which downloads the Android command-line tools, Gradle, and the required SDK packages automatically (edit the paths at the top of the script first if you don't want the defaults under `c:\_dev`).

## Play Store / release builds

Release builds use a local upload keystore. Secrets are **not** in git.

1. Copy [`keystore.properties.example`](keystore.properties.example) to `keystore.properties`.
2. Generate an upload key (once) and keep a backup of the `.jks` **and** passwords:
   ```
   keytool -genkeypair -v -keystore keystore/pocketarrows-upload.jks -alias pocketarrows -keyalg RSA -keysize 2048 -validity 10000
   ```
3. Fill `storeFile`, `storePassword`, `keyAlias`, and `keyPassword` in `keystore.properties`.
4. Build the signed release APK:
   ```
   ./gradlew assembleRelease
   ```
   Output: `build/outputs/apk/release/PocketArrows-release.apk`.

Create the Play Console app with application id `com.stockds.pocketarrows` and store title PocketArrows, then upload that APK as the first release.
