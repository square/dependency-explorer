
# Dependency Explorer

CLI tool that explores dependencies for Android Gradle based projects.

The purpose of this tool is to be able to provide all the dependencies of a given project, transitively. This tool does not invoke Gradle, making it appealing for usages where performance is preferred. 




## Environment Variables

To run this project, you will need to add the following environment variables to your system environment.

`export REPO_HOME`=/path/to/your/repo



## Run Locally

Clone the project

```bash
  git clone git@github.com:square/dependency-explorer.git
```

Go to the project directory

```bash
  cd app
```

Development

```
 ./gradlew :run --args="gradle-projects apps/spos/app" 
 ```

Production
 ```
 ./binary/bin/dependency-explorer gradle-projects apps/spos/app
 ```

Output

```
Collecting module dependencies for inputs :apps:spos:app
Importing 2966 total modules
Wrote dependencies to /Users/phundal/Development/android-register/settings_modules_override.gradle
Executed in: 650 ms
```

## Authors

- [@phundal](https://github.com/paulhundal)


## Roadmap

- Add ability to configure rules for dependencies
- Add ability to pass in default values


## Releasing

Update version in root build.gradle

Generate New Binary

```
./gradlew :buildBinary
```

## Contributing

1. Fork the project
2. Create a PR for review

That's it! :) 
