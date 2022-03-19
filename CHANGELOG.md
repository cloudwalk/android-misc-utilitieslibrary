# CHANGELOG

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
- Update dependency list to shrink final package.

## [1.0.0] - 2021-09-30
- Initial release.
