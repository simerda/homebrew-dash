FROM gradle:jdk17
COPY . /src/homebrew-dash
WORKDIR /src/homebrew-dash

# install python and meross IoT library
RUN apt-get update
RUN apt-get install -y --no-install-recommends tzdata python3 python3-pip
RUN pip3 install --no-cache-dir meross_iot==0.4.6.0
RUN python3 --version

RUN ./gradlew build
RUN rm -f build/libs/*-plain.jar

CMD java -jar build/libs/*.jar
