FROM gradle:jdk17
COPY . /src/homebrew-dash
WORKDIR /src/homebrew-dash
RUN gradle bootJar
RUN rm -f build/libs/*-plain.jar

CMD java -jar build/libs/*.jar
