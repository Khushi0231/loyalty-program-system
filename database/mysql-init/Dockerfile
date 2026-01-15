# MySQL Database Initialization Container
FROM mysql:8.0

# Set environment variables
ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=loyalty_db

# Copy initialization scripts
COPY schema.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY seed-data.sql /docker-entrypoint-initdb.d/02-seed-data.sql

# Expose default MySQL port
EXPOSE 3306

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD mysqladmin ping -h localhost -u root -ppassword || exit 1

# Set command to allow custom configuration
CMD ["--default-authentication-plugin=mysql_native_password"]

