module dev.rico.metrics {

    exports dev.rico.metrics;
    exports dev.rico.metrics.types;
    exports dev.rico.internal.metrics to dev.rico.metrics.server,
            dev.rico.metrics.server.javaee,
            dev.rico.metrics.server.spring;

    requires transitive dev.rico.core;
    requires micrometer.core;
    requires org.slf4j;
}
