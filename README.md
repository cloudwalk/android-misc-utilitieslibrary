# Utilities Library

Several projects share the same fundamental requirements. These are usually
detached from business logic, such as data type conversions or recurrent
queries to the OS. `io.cloudwalk.utilitieslibrary` centralizes these basic
features for easier maintenance and reusage.  

## Local dependencies

Local dependencies are those which are _private_, but within the scope of the
`io.cloudwalk.utilitieslibrary` development team. They need to be made
available locally before the library can be built:  

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
