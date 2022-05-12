How to run the application:

Run via IDE Just run java class as Java Application -> com.akg.imdgperfcheck.service.ImdgPerfCheckApplication.java

Build with maven and Run the jar file.
1. Go to the project home directory and run
  mvn clean install
2. Copy/Paste imdgperfcheck-0.0.1-SNAPSHOT.jar and src/main/resources directory contents without docker directory where you want, for example let be directory TEST.
  Example how test directory will look like:
  ```
  ...
    |_test
         |_application.yml
         |_application-hazelcast.yml
         |_application-redis.yml
         |_imdgperfcheck-0.0.1-SNAPSHOT.jar
         |_winemag-data_first150k.csv
  ```
3. Go to TEST directory open application.yaml file and set the proper values for the properties ( input-data-file, use-cases, etc.
  Example snippets of application.yaml:
  ```
      spring:
        profiles: # here is specified the actual DB that will be benchmarked
            active: redis # possible values are: redis, hazelcast
      ...
      commons:
        active-use-case: crud # possible values are: one, crud
        input-data-file: winemag-data_first150k.csv # source file with the data
        ...
        use-cases:
          # Execution type possible values: 
          #   what: elasticsearch, mongodb, cassandra, postgres, ksqldb
          #   mode: inserts, search, updates, deletes
          ...
          crud:
            - {what: '${spring.profiles.active}', mode: inserts, time-in-ms: 1800000} 
            - {what: '${spring.profiles.active}', mode: search, time-in-ms: 300000} 
            - {what: '${spring.profiles.active}', mode: updates, time-in-ms: 60000}
            - {what: '${spring.profiles.active}', mode: deletes, time-in-ms: 60000}
       ...
  ```
4. Run.
  java -jar imdgperfcheck-0.0.1-SNAPSHOT.jar -Dspring.config.location=.

5. Configurations
  The main configuration is at src/main/resources/application.yml
Each DB has its own file for example for Reds the file is src/main/resources/application-redis.yml.
Docker setup can be found at directory src/main/resources/docker. Each DB has its own sub-directory, for example, Hazelcast directory is src/main/resources/docker/hazelcast.
When the particular docker container is run it has to be run commands that are important for proper setup, for example, the creation of the indexes. For each DB example of such commands can be found at docker folder -> https://github.com/atanasgegov/imdg-perf-check/blob/main/src/main/resources/docker/read-and-exec-docker-helpful-commands-before-to-run-the-tests.txt
