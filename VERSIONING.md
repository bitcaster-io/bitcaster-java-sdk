# Versioning

The project version is defined in `pom.xml`:

```xml
<version>0.1.0-SNAPSHOT</version>
```

Use semantic versioning. Development versions use the `-SNAPSHOT` suffix; released versions do not.

## Bump a Development Version

Change the version manually in `pom.xml`, or use the Maven Versions Plugin:

```bash
./mvnw versions:set -DnewVersion=0.2.0-SNAPSHOT
./mvnw versions:commit
```

## Prepare a Release

Set the release version and validate the build:

```bash
./mvnw versions:set -DnewVersion=0.2.0
./mvnw clean verify
```

The optional coverage build is also available:

```bash
./mvnw -Pcoverage verify
```

The coverage report is generated at `target/site/jacoco/index.html`.

## Tag a Release

After the release build succeeds, create and push the matching Git tag:

```bash
git tag v0.2.0
git push origin v0.2.0
```

A typical version sequence is:

```text
0.1.0-SNAPSHOT -> 0.1.0 -> 0.2.0-SNAPSHOT
```
