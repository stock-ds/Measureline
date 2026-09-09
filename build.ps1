# Build a debug APK you can sideload onto an Android device.
# Usage (from this folder):  .\build.ps1

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$apk = Join-Path $PSScriptRoot "build\outputs\apk\debug\PocketArrows-debug.apk"

Write-Host "Building PocketArrows debug APK..."
& .\gradlew.bat assembleDebug
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

if (-not (Test-Path $apk)) {
    Write-Error "Build finished but APK was not found at $apk"
}

Write-Host ""
Write-Host "APK ready:"
Write-Host "  $apk"
Write-Host ""
Write-Host "Sideload with:"
Write-Host "  adb install -r `"$apk`""

function Get-AdbPath {
    $cmd = Get-Command adb -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }

    $localProps = Join-Path $PSScriptRoot "local.properties"
    if (-not (Test-Path $localProps)) { return $null }

    $sdkLine = Get-Content $localProps | Where-Object { $_ -match '^\s*sdk\.dir=' } | Select-Object -First 1
    if (-not $sdkLine) { return $null }

    $sdk = $sdkLine.Substring($sdkLine.IndexOf("=") + 1).Trim().Trim("'").Trim('"')
    $sdk = $sdk -replace '\\\\', '\'
    $candidate = Join-Path $sdk "platform-tools\adb.exe"
    if (Test-Path $candidate) { return $candidate }
    return $null
}

$adb = Get-AdbPath
if (-not $adb) {
    Write-Host ""
    Write-Host "adb not found. Copy the APK to your phone, or install Android platform-tools and re-run."
    exit 0
}

$devices = & $adb devices
$ready = $devices | Where-Object { $_ -match "\tdevice$" }
if (-not $ready) {
    Write-Host ""
    Write-Host "No Android device in 'device' state. Plug in a phone with USB debugging, then:"
    Write-Host "  adb install -r `"$apk`""
    exit 0
}

Write-Host ""
Write-Host "Installing onto connected device..."
& $adb install -r $apk
exit $LASTEXITCODE
