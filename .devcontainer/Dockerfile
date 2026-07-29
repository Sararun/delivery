FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

ARG MAVEN_VERSION=3.9.9
RUN curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
        -o /tmp/maven.tar.gz \
 && tar -xzf /tmp/maven.tar.gz -C /opt \
 && ln -sfn "/opt/apache-maven-${MAVEN_VERSION}" /opt/maven \
 && rm /tmp/maven.tar.gz
ENV MAVEN_HOME=/opt/maven
ENV PATH="/opt/maven/bin:${PATH}"

RUN find /etc/apt/sources.list.d/ -name '*.list' -exec grep -l 'dl.yarnpkg.com' {} + 2>/dev/null | xargs -r rm -f \
 && apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends postgresql-client \
 && apt-get clean && rm -rf /var/lib/apt/lists/*


RUN mkdir -p /home/vscode/.m2 /workspace \
 && chown -R vscode:vscode /home/vscode/.m2 /workspace \
 && ln -sfn /opt/maven/bin/mvn /usr/local/bin/mvn \
 && ln -sfn /opt/maven/bin/mvnDebug /usr/local/bin/mvnDebug

WORKDIR /workspace
USER vscode
