# Utilities Library

Several projects share the same fundamental requirements. These are usually
detached from business logic, such as data type conversions or recurrent
queries to the OS. `io.cloudwalk.utilitieslibrary` centralizes these basic
features for easier maintenance and reusage.

## Local dependencies

Local dependencies are those which are _private_, but within the scope of the
Utilities Library development team. They need to be made available locally
before the library can be built:  

1. Clone the repository [android-misc-loglibrary](https://github.com/mauriciospinardi/android-misc-loglibrary)
2. Follow the instructions to [locally publish](#local-publishing) it.

## Local publishing

1. Rebuild the `release` variant based on a tag or commit of your choice.
2. Run task: `gradle publishToMavenLocal`

Local publishing within this repository is actually set to use the `release`
build variant only.

## Development notes

Those which intend to consume `io.cloudwalk.utilitieslibrary` and have already
extended `android.app.Application` will face a fatal failure at build time.
There are two options to bypass such failure, keeping all of the library
original features:

1. Extend `io.cloudwalk.utilitieslibrary.Application` instead of
   `android.app.Application` and ensure to include `tools:replace="android:name"`
   in the application's `AndroidManifest.xml`.
    - `io.cloudwalk.utilitieslibrary.Application` was designed to merely
      intercept and cache the application instance for internal consumption. It
      doesn't change `android.app.Application` behavior. No side effects are
      expected.
2. Include `tools:replace="android:name"` in the application's
   `AndroidManifest.xml` and invoke
   `io.cloudwalk.utilitieslibrary.Application#setInstance(android.app.Application)`
   prior to any other calls to `io.cloudwalk.utilitieslibrary`.
