# vehicle-registration-backend-micro
Microservice version of same

## IntelliJ note after refactors

After moving or renaming packages/classes, IntelliJ may show false dependency errors (for example unresolved `JpaRepository` or repository `save(...)` methods) even when Gradle builds successfully.

If that happens:

1. In the Gradle tool window, click the refresh icon (two arrows) to load Gradle changes.
2. If errors remain, run **File -> Invalidate Caches... -> Invalidate and Restart**.

Avoid adding libraries directly from IntelliJ quick-fixes for this project; keep dependencies managed in `build.gradle`.

