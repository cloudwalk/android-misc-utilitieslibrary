# CHANGELOG

## [1.1.0] - YYYY-MM-DD
- Replace `DataUtility` by `BundleUtility`, `ByteUtility` and `StringUtility`.

## [1.0.14] - 2022-06-08
- Update `DataUtility#getHexStringFromByteArray(byte, int, int)` modifier from
  `private` to `public`.
- Remove annotations from primitives.
- Update documentation.

## [1.0.13] - 2022-06-03
- Repurpose `DataUtility#getIntFromByteArray(byte, int)` to consider the pure
  value of each byte in the input array.
- Overload `byte` API to allow partial conversion:  
  - `DataUtility#getHexStringFromByteArray(byte, int, int)`
  - `DataUtility#getIntFromByteArray(byte, int, int)`
- Review CHANGELOG.md content.

## [1.0.12] - 2022-03-21
- Replace `Application#getPackageContext()` by `Application#getContext()`.

## [1.0.11] - 2022-03-18
- Completely remove incomplete support to package lifecycle observers.

## [1.0.10] - 2022-02-01
- Ensure `ServiceUtility#unregister(String, String)` will run synchronously.

## [1.0.9] - 2022-01-28
- Turn bug into feature: so far, all service connection attempts following a
  successful one would silently be dropped. From now on, each attempt will
  internally trigger `ServiceUtility#unregister(String, String)`, to ensure
  proper feedback.
- Update code patterns and internal dependencies.

## [1.0.8] - 2021-12-17
- Update build tools.

## [1.0.7] - 2021-11-29
- Update internal dependencies.

## [1.0.6] - 2021-11-29
- Update internal dependencies.

## [1.0.5] - 2021-11-26
- Update logs.

## [1.0.4] - 2021-11-16
- Update logs.

## [1.0.3] - 2021-10-11
- Extend service registering by allowing extra data transferring attached to
  the caller intent.

## [1.0.2] - 2021-10-05
- Update inner conversion from `int` to `byte[]` at `CRC16_XMODEM(byte[])`.

## [1.0.1] - 2021-10-04
- Erase unused dependencies from the dependency list.

## [1.0.0] - 2021-09-30
- Initial release.
