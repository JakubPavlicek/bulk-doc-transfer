FROM quay.io/wildfly/wildfly:38.0.0.Final-jdk21

COPY --chown=jboss:root target/jms-app-1.0.0.war $JBOSS_HOME/standalone/deployments/

RUN chmod -R ug+rwX $JBOSS_HOME