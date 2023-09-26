FROM gradle:jdk17
COPY . /src/homebrew-dash
WORKDIR /src/homebrew-dash
RUN ./gradlew build
RUN rm -f build/libs/*-plain.jar

CMD java -jar build/libs/*.jar
