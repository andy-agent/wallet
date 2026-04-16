#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ndk_home="${NDK_HOME:-/Users/cnyirui/Library/Android/sdk/ndk/29.0.14206865}"

if [[ ! -d "$ndk_home" ]]; then
  echo "Android NDK not found at $ndk_home"
  exit 1
fi

tmpdir="$(mktemp -d)"
cleanup() {
  rm -rf "$tmpdir"
}
trap 'echo -e "Aborted, error $? in command: $BASH_COMMAND"; cleanup; exit 1' ERR INT

mkdir -p "$tmpdir/jni"
pushd "$tmpdir" >/dev/null

echo 'include $(call all-subdir-makefiles)' > jni/Android.mk
ln -s "$__dir/third_party/hev-socks5-tunnel" jni/hev-socks5-tunnel

"$ndk_home/ndk-build" \
  NDK_PROJECT_PATH=. \
  APP_BUILD_SCRIPT=jni/Android.mk \
  "APP_ABI=armeabi-v7a arm64-v8a x86 x86_64" \
  APP_PLATFORM=android-24 \
  NDK_LIBS_OUT="$tmpdir/libs" \
  NDK_OUT="$tmpdir/obj" \
  "APP_CFLAGS=-O3 -DPKGNAME=com/v2ray/ang/service" \
  "APP_LDFLAGS=-Wl,--build-id=none -Wl,--hash-style=gnu"

mkdir -p "$__dir/libs"
cp -rf "$tmpdir/libs/"* "$__dir/libs/"

popd >/dev/null
cleanup
