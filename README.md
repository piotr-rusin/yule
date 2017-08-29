# Yule

A configurable, database-backed blog engine.

## Features

* paginated list of published blog posts, displayed on the main page
* blog pages displayed in the top navigation menu
* admin panel with a paginated table of articles and a form page for creating and editing them
* saving posts and pages with different statuses
* automatic, schedulable publication of posts and pages
* config-file based configuration
* automatic database schema migrations with custom scripts
* unit and integration tests

## Configuration

Before running the blog engine, it is necessary to provide a set of configuration option values. They can be provided in an application.yml file.

An example of complete configuration:

```yaml
spring:
  datasource:
    # a PostgreSQL database URL
    url: jdbc:postgresql://localhost/your_database
    username: your_database_user
    password: your_database_users_password
yule:
  user:
    #a non-empty string, optional, default: "admin"
    login: your_login
    # any string from 6 to 20 character long
    password: your_password
    # a non-empty string, optional, equal to login by default
    name: Your Name
    # a valid email address, optional
    email: your.email@example.com
  # a non-empty string, optional, default is "Default blog title"
  title: Example blog title
  # a non-empty string, optional
  description: Another Yule blog
  # a number of published blog posts displayed on a page of the blog post
  # list
  # at least 5, optional, default is 5
  indexPageSize: 6
  # a number of articles to be displayed on a page of the article list
  # available on the admin panel
  # at least 5, optional, default is 10
  adminArticleListPageSize: 7
```

An example of minimal configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost/your_database
    username: your_database_user
    password: your_database_users_password
yule:
  user:
    password: your_password
```

To be able to run tests, provide additional configuration in test profile. You can do so by adding a separat section to the configuration file, to be loaded when test profile is active. Example:

```
# The rest of the configuration options is specified before this line
---

spring:
  profiles: test
  datasource:
    url: jdbc:postgresql://localhost/yule_test_database
```

## Running and installation

### Running with Maven

You can test the project by running it from the project directory:

```bash
$ git clone https://github.com/piotr-rusin/yule
$ cd yule
$ mvn spring-boot:run -Dspring.config.location="file:/path/to/application.yml"
```

To run unit and integration tests, execute:

```bash
$ mvn test -Dspring.config.location="file:/path/to/application.yml"
```

### Running as an executable jar

The project uses spring-boot-maven-plugin configured to create an executbale jar. To build and run it, execute:

```bash
$ mvn package
$ target/yule-0.0.1-SNAPSHOT.jar --spring.config.location="file:/path/to/application.yml"
```

The jar can be moved anywhere and executed from any directory. Instead of passing the path to the condifuration file as the command line option, the file can be put under the same directory as the jar, or in a config sub-directory of the directory, and it will be loaded by the executable from there.

Once the project is running, the configured blog can be viewed at [localhost:8080 URL](localhost:8080). The admin panel is available at [localhost:8080/admin](localhost:8080/admin), after logging in to it with the login and password specified in the config file.

### Installing the jar

Instead of being executed from a directory manually or with a custom script, the jar file may be installed as a [Unix/Linux service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-service).

## Built with

* [Spring Boot][1], [Hibernate ORM][2], [PostgreSQL][3], [Thymeleaf][4], [jQuery][5] - core functionality
* [Flyway][6] - database schema migration
* [JUnit][7], [AssertJ][8], [Mockito][9] - unit and integration tests
* [Maven][10] - project management

[1]: https://projects.spring.io/spring-boot/
[2]: http://hibernate.org/orm/
[3]: https://www.postgresql.org/
[4]: http://www.thymeleaf.org/
[5]: https://jquery.com/
[6]: https://flywaydb.org/
[7]: http://junit.org/junit4/
[8]: http://joel-costigliola.github.io/assertj/
[9]: http://site.mockito.org/
[10]: https://maven.apache.org/

## License

MIT <br>
See [LICENSE](https://github.com/piotr-rusin/yule/blob/master/LICENSE)

