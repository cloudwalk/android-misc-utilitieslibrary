# Utilities Library

<img src="SHIELD.svg"/><br/>

Several projects share the same fundamental requirements. These are usually
detached from business logic, such as data type conversions or recurrent
queries to the OS. `io.cloudwalk.utilitieslibrary` centralizes these basic
features for easier maintenance and reusage.  

## Project dependencies

Due to its very fundamental scope, `io.cloudwalk.utilitieslibrary` was designed
to be independent of copyrighted[^1] packages. However, there are
[local dependencies](#local-dependencies) to be taken into account.  

[^1]: Those provided by third parties, usually under NDA.

### Local dependencies

Local dependencies are those within the scope of the
`io.cloudwalk.utilitieslibrary` development team. They need to be made
available before the library can be built:  

1. Clone the repository [android-misc-loglibrary](https://github.com/cloudwalk/android-misc-loglibrary)
2. Follow the instructions to [locally publish](#local-publishing) it.

## Local publishing

1. Rebuild the `release` variant based on a tag or commit of your choice.
2. Run task: `gradle publishToMavenLocal`

Local publishing within this repository is actually set to use the `release`
build variant only.  

## Development notes

Due to `io.cloudwalk.utilitieslibrary` dependency on `io.cloudwalk.loglibrary`
it carries all of its disclosures. Be sure to check its
[README.md](https://github.com/cloudwalk/android-misc-loglibrary/blob/main/README.md)
for further details.  
