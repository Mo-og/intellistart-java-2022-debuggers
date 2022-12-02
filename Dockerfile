FROM openjdk:11
ADD /target/interview-planning-0.0.1-SNAPSHOT.jar debuggers.jar
ENTRYPOINT ["java","-jar","debuggers.jar"]