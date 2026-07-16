# Setup script for Android SDK and Gradle compiling sandbox

$sdkDir = "c:\_dev\android-sdk"
$gradleDistDir = "c:\_dev\gradle-dist"
$workspace = "c:\_dev\Beats"

Write-Host "Creating directories..."
if (!(Test-Path $sdkDir)) { New-Item -ItemType Directory -Path $sdkDir }
if (!(Test-Path $gradleDistDir)) { New-Item -ItemType Directory -Path $gradleDistDir }

# 1. Download & Install Android Command Line Tools
$cmdlineZip = "$sdkDir\cmdline-tools.zip"
$cmdlineUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

if (!(Test-Path "$sdkDir\cmdline-tools\latest\bin\sdkmanager.bat")) {
    Write-Host "Downloading Android SDK Command-line Tools..."
    Invoke-WebRequest -Uri $cmdlineUrl -OutFile $cmdlineZip
    
    Write-Host "Extracting Command-line Tools..."
    Expand-Archive -Path $cmdlineZip -DestinationPath "$sdkDir\temp"
    
    # Arrange according to required folder structure: sdk-root/cmdline-tools/latest/bin/...
    New-Item -ItemType Directory -Path "$sdkDir\cmdline-tools"
    Move-Item -Path "$sdkDir\temp\cmdline-tools" -Destination "$sdkDir\cmdline-tools\latest"
    
    # Cleanup temp
    Remove-Item -Recurse -Force "$sdkDir\temp"
    Remove-Item -Force $cmdlineZip
    Write-Host "Command-line tools installed."
} else {
    Write-Host "Command-line tools already installed."
}

# 2. Download & Install Gradle
$gradleZip = "$gradleDistDir\gradle.zip"
$gradleUrl = "https://services.gradle.org/distributions/gradle-8.2-bin.zip"
$gradleBin = "$gradleDistDir\gradle-8.2\bin\gradle.bat"

if (!(Test-Path $gradleBin)) {
    Write-Host "Downloading Gradle 8.2..."
    Invoke-WebRequest -Uri $gradleUrl -OutFile $gradleZip
    
    Write-Host "Extracting Gradle..."
    Expand-Archive -Path $gradleZip -DestinationPath $gradleDistDir
    
    Remove-Item -Force $gradleZip
    Write-Host "Gradle installed."
} else {
    Write-Host "Gradle already installed."
}

# 3. Accept Licenses and Install Android SDK Packages
Write-Host "Accepting Android SDK licenses..."
@("y") * 100 | & "$sdkDir\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root="$sdkDir" --licenses

Write-Host "Installing SDK Platform 34, Build Tools 34.0.0, and Platform Tools..."
& "$sdkDir\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root=$sdkDir "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 4. Generate Gradle Wrapper in project root
if (!(Test-Path "$workspace\gradlew.bat")) {
    Write-Host "Initializing Gradle wrapper..."
    Set-Location $workspace
    & $gradleBin wrapper
}

Write-Host "Environment bootstrap completed successfully!"
