# Building Weather Refind

Each port is a separate Gradle project. All nine projects include the shared code in `common/src/main/java`. Keep the repository layout intact when building a port.

| Directory | Build JDK | Gradle |
| --- | --- | --- |
| `Forge/1.20.1` | 17 | 8.14.3 |
| `NeoForge/1.21.1` | 21 | 8.14.3 |
| `Fabric/1.20.1` | 21 | 8.12 |
| `Fabric/1.21.11` | 25 | 9.8.0 |
| `Fabric/26.1` | 25 | 9.8.0 |
| `Fabric/26.2` | 25 | 9.8.0 |
| `Fabric/26.3` | 25 | 9.8.0 |
| `NeoForge/26.2` | 25 | 9.8.0 |
| `NeoForge/26.3` | 25 | 9.8.0 |

Install the listed JDK, set `JAVA_HOME`, and run from the port's directory:

```sh
./gradlew build --no-daemon
```

On Windows:

```powershell
.\gradlew.bat build --no-daemon
```

The wrapper downloads the required Gradle version on first use. An internet connection is required to download MC, mappings and build dependencies. The NeoForge 26.3 project targets the beta loader 26.3.0.22-beta.

The installable jar is written to `build/libs/`. Fabric 1.20.1 builds with JDK 21 and targets Java 17. Fabric 1.21.11 builds with JDK 25 and targets Java 21. Do not install jars ending in `-sources.jar` or `-dev.jar`.

Compilation and packaging checks do not launch the game or measure performance.

The shared weather rules have a standalone regression check. From the repository root, with JDK 17 or newer:

```sh
javac -d common/build/test-classes common/src/main/java/dev/norevy/weatherrefind/WeatherMath.java common/src/main/java/dev/norevy/weatherrefind/WeatherMode.java common/src/test/java/dev/norevy/weatherrefind/WeatherMathTest.java
java -cp common/build/test-classes dev.norevy.weatherrefind.WeatherMathTest
```

The source code is subject to [LICENSE](LICENSE).
