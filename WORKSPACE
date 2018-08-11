load("@bazel_tools//tools/build_defs/repo:maven_rules.bzl", "maven_jar", "maven_dependency_plugin")
load(":junit5.bzl", "junit_jupiter_java_repositories", "junit_platform_java_repositories")

maven_server(
    name = "default"
)

junit_jupiter_java_repositories()
junit_platform_java_repositories()

maven_jar(
    name = "org_apache_commons_commons_lang3",
    artifact = "org.apache.commons:commons-lang3:3.7"
)

maven_jar(
    name = "commons_codec_commons_codec",
    artifact = "commons-codec:commons-codec:1.11"
)

maven_jar(
    name = "org_springframework_spring_core",
    artifact = "org.springframework:spring-core:5.0.7.RELEASE"
)

maven_jar(
    name = "org_slf4j_slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.7.25"
)

maven_jar(
    name = "ch_qos_logback_logback_classic",
    artifact = "ch.qos.logback:logback-classic:1.2.3"
)

maven_jar(
    name = "ch_qos_logback_logback_core",
    artifact = "ch.qos.logback:logback-core:1.2.3"
)

maven_jar(
    name = "com_google_jimfs_jimfs",
    artifact = "com.google.jimfs:jimfs:1.1"
)

maven_jar(
    name = "com_google_guava_guava",
    artifact = "com.google.guava:guava:25.1-jre"
)
