# My ever first Java Project

## Build

Run following maven commands in order:

1. `mvn clean -f "./pom.xml"`
2. `mvn validate -f "./pom.xml"`
3. `mvn test -f "./pom.xml"`
4. `mvn test-compile -f "./pom.xml"`
5. `mvn package -f "./pom.xml"`

## Run

`java -jar ./target/xlsx_reader-1.0.jar ./test.xlsx`
