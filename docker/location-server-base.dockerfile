FROM ubuntu
MAINTAINER whereat.admin@riseup.net

# expose port 5000 to host
EXPOSE 5000

# add package dependencis
RUN echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN apt-get -y install apt-transport-https # necessary to update sbt package added above
RUN apt-get -y update

# install packages
RUN apt-get -y install default-jdk
RUN apt-get -y install sbt

# set working directory
RUN mkdir whereat-location-server
WORKDIR whereat-location-server

