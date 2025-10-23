FROM quay.io/wildfly/wildfly:38.0.0.Final-jdk21

USER jboss

# Add JMS queue using the WildFly management CLI
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=/dev/stdin <<EOF
embed-server --std-out=echo --server-config=standalone-full.xml
/subsystem=messaging-activemq/server=default/jms-queue=submissionQueue:add(entries=["java:/jms/queue/submissionQueue"])
stop-embedded-server
EOF

COPY --chown=jboss:root target/jms-app-1.0.0.war $JBOSS_HOME/standalone/deployments/

RUN chmod -R ug+rwX $JBOSS_HOME