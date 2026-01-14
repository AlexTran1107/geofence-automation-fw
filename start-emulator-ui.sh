#!/bin/bash
# Script to start Android Emulator with UI visible (not headless)
# This ensures you can see the emulator window while tests are running

EMULATOR_NAME="Medium_Phone_API_36.0"
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"

echo "=========================================="
echo "Starting Android Emulator with UI Visible"
echo "=========================================="
echo "Emulator: $EMULATOR_NAME"
echo "Android SDK: $ANDROID_HOME"
echo ""

# Check if emulator is already running
if adb devices | grep -q "emulator"; then
    echo "⚠️  Emulator is already running."
    echo "Current devices:"
    adb devices
    echo ""
    read -p "Do you want to kill existing emulator and start fresh? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Killing existing emulator processes..."
        adb emu kill
        pkill -f qemu-system
        sleep 2
    else
        echo "Using existing emulator instance."
        exit 0
    fi
fi

# Check if emulator binary exists
EMULATOR_BIN="$ANDROID_HOME/emulator/emulator"
if [ ! -f "$EMULATOR_BIN" ]; then
    echo "❌ Error: Emulator not found at $EMULATOR_BIN"
    echo "Please set ANDROID_HOME environment variable correctly."
    exit 1
fi

# Start emulator with UI visible (no headless flag)
echo "🚀 Starting emulator with UI visible..."
echo "   This may take 30-60 seconds..."
echo ""

# Start emulator in background but with UI window
nohup "$EMULATOR_BIN" \
    -avd "$EMULATOR_NAME" \
    -no-snapshot-load \
    -wipe-data \
    > /dev/null 2>&1 &

EMULATOR_PID=$!
echo "Emulator process started (PID: $EMULATOR_PID)"
echo ""

# Wait for emulator to boot
echo "⏳ Waiting for emulator to boot..."
adb wait-for-device

echo "⏳ Waiting for boot to complete..."
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'

echo ""
echo "✅ Emulator is ready!"
echo ""
echo "Connected devices:"
adb devices
echo ""
echo "📱 Emulator window should be visible on your screen."
echo "   If you don't see it, check your desktop/window manager."
echo ""
echo "To stop the emulator, run: adb emu kill"

