FROM whereat/whereat-location-server:0.1

EXPOSE 5000

ADD https://s3-us-west-2.amazonaws.com/whereat-location-server/whereat-server-assembly.jar /

ENV WHEREAT_ENVIRONMENT="PRODUCTION"
ENV WHEREAT_PKP_REPORT_URI="http://whereat.io/pkp-report"
ENV WHEREAT_PKP_MAX_AGE="500000"

# The following additional environment variables must be set:
# WHEREAT_PKP_PUBLIC_KEY, WHEREAT_PKP_BACKUP_KEY, WHEREAT_PKP_EMERGENCY_KEY

CMD ["java", "-jar", "whereat-server-assembly.jar"]