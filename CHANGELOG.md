# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 03-10-2022

### Changed
- `jakarta-data-api`
  - Removed `hamcrest-all` in favour of `assertj-core`
  - Changed the assertions in `PageableTest` and `SortTest`
  - Removed redundant tests on `SortTest`
- `jakarta-data-parent`
  - Updated the following libraries
    - `mockito`.version` to `4.8.0`
    - `junit` to `5.9.0`
  - Fix typo on `mockito.version` 
 