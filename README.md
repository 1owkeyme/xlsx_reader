# My ever first Java Project

# Table of Contents

- [My ever first Java Project](#my-ever-first-java-project)
- [Table of Contents](#table-of-contents)
- [Build](#build)
- [Run](#run)
- [Task Reference](#task-reference)

# Build

Run following maven commands in order:

1. `mvn clean -f "./pom.xml"`
2. `mvn validate -f "./pom.xml"`
3. `mvn test -f "./pom.xml"`
4. `mvn test-compile -f "./pom.xml"`
5. `mvn package -f "./pom.xml"`

# Run

`java -jar ./target/xlsx_reader-1.0.jar ./test.xlsx`

# Task Reference

1. Прочитайте файл Excel
2. Создайте Java-классы в соответствии с предложенной схемой
3. Для каждой строки в файле создайте соответствующие объекты Java
4. Пусть программа посчитает и выведет в консоль следующие значения:
   a. Количество физических лиц среди сотрудников
   b. Количество компаний среди сотрудников
   c. Имя и фамилия сотрудников, которым меньше 20 лет
