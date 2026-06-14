# SFTP to PostgreSQL Batch Processing System

## Overview

This project demonstrates a production-style data ingestion pipeline using Spring Boot, Spring Batch, Spring Integration SFTP, and PostgreSQL.

The application periodically polls an SFTP server, downloads CSV files, processes large datasets efficiently using Spring Batch, skips invalid records, retries transient failures, and performs UPSERT operations into PostgreSQL.

---

## Use Case

A retail inventory system generates sales CSV files and uploads them to an SFTP server.

The application:

- Connects to the SFTP server.
- Downloads newly available CSV files.
- Processes records in chunks.
- Skips malformed records.
- Retries transient database failures.
- Supports restartability.
- UPSERTS records into PostgreSQL.

---

## Architecture

```
SFTP Server
     │
     ▼
Spring Integration SFTP Poller
     │
     ▼
Downloads CSV files
to Local Directory
     │
     ▼
Launch Spring Batch Job
     │
     ▼
FlatFileItemReader
     │
     ▼
ItemProcessor
     │
     ▼
JdbcBatchItemWriter (UPSERT)
     │
     ▼
PostgreSQL
```

---

## Features

### SFTP Integration

- Polls SFTP server every 15 seconds.
- Downloads CSV files matching configured patterns.
- Prevents duplicate downloads.
- Preserves timestamps.
- Supports password authentication.
- Can be extended to SSH key authentication.

---

### Spring Batch Features

- Chunk-oriented processing.
- Handles large CSV files efficiently.
- Restartable jobs.
- Metadata persisted in PostgreSQL.
- Fault tolerance.
- Skip invalid records.
- Retry transient database failures.

---

### Database Features

- PostgreSQL integration.
- JDBC batch writing.
- UPSERT support using `ON CONFLICT`.
- Batch inserts/updates.

---

## Technology Stack

| Technology | Version |
|------------|----------|
| Java | 21 |
| Spring Boot | 4.x |
| Spring Batch | 6.x |
| Spring Integration SFTP | 7.x |
| PostgreSQL | 17 |
| Docker Desktop | Latest |
| Atmoz SFTP | Latest |
| Maven | 3.x |

---

## Project Structure

```
src/main/java
├── config
│   ├── SftpConfig.java
│   ├── IntegrationConfig.java
│   ├── ReaderConfig.java
│   ├── ProcessorConfig.java
│   ├── WriterConfig.java
│   ├── StepConfig.java
│   └── JobConfig.java
│
├── dto
│   └── SalesRecord.java
│
├── processor
│   └── InventoryProcessor.java
│
├── listener
│   └── InventorySkipListener.java
│
└── BatchProcessingDemoApplication.java

src/main/resources
└── application.properties
```

---

## Data Flow

### Phase 1: CSV Upload

CSV files are uploaded to the SFTP server.

Example:

```
/upload/sales.csv
```

---

### Phase 2: SFTP Polling

Spring Integration polls the server.

```
Every 15 seconds
```

Downloads files to:

```
D:/sftp-demo/local-input
```

---

### Phase 3: Batch Job Launch

Each downloaded file launches:

```
inventoryJob
```

Parameters:

```
inputFile=<downloaded file path>
run.id=<timestamp>
```

---

### Phase 4: CSV Reading

Uses:

```
FlatFileItemReader
```

Responsibilities:

- Reads CSV line by line.
- Skips header row.
- Maps columns to DTO.

---

### Phase 5: Processing

Uses:

```
ItemProcessor
```

Responsibilities:

- Validate records.
- Reject invalid records.
- Transform data if required.

---

### Phase 6: Database Load

Uses:

```
JdbcBatchItemWriter
```

Responsibilities:

- Batch database writes.
- UPSERT existing records.

---

## Sample CSV

```csv
s_no,item_name,quantity_sold,price,remaining_stock
1,Salt,10,50,40
2,Sugar,5,40,20
3,Oil,8,120,12
4,Soap,15,30,5
5,Biscuits,20,10,25
6,Chocolates,27,20,40
```

---

## Database Setup

Create database:

```sql
CREATE DATABASE inventory_db;
```

---

Create table:

```sql
CREATE TABLE sales (
    s_no INTEGER PRIMARY KEY,
    item_name VARCHAR(100),
    quantity_sold INTEGER,
    price NUMERIC(10,2),
    remaining_stock INTEGER
);
```

---

## UPSERT Logic

```sql
INSERT INTO sales (
    s_no,
    item_name,
    quantity_sold,
    price,
    remaining_stock
)
VALUES (
    :sNo,
    :itemName,
    :quantitySold,
    :price,
    :remainingStock
)
ON CONFLICT (s_no)
DO UPDATE SET
    item_name = EXCLUDED.item_name,
    quantity_sold = EXCLUDED.quantity_sold,
    price = EXCLUDED.price,
    remaining_stock = EXCLUDED.remaining_stock;
```

---

## Docker SFTP Setup

Pull image:

```bash
docker pull atmoz/sftp
```

Run container:

```bash
docker run -d --name sftp-server -p 2222:22 -v D:\sftp-demo\incoming:/home/demo/upload atmoz/sftp demo:demo123:::upload
```

---

## SFTP Credentials

| Property | Value |
|-----------|---------|
| Host | localhost |
| Port | 2222 |
| Username | demo |
| Password | demo123 |
| Remote Directory | /upload |

---

## Local Directories

Incoming:

```
D:\sftp-demo\incoming
```

Downloaded Files:

```
D:\sftp-demo\local-input
```

---

## application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=false

sftp.host=localhost
sftp.port=2222
sftp.username=demo
sftp.password=demo123
sftp.remote-directory=/upload
sftp.local-directory=D:/sftp-demo/local-input
```

---

## Skip Logic

Configured to skip:

```java
ValidationException
FlatFileParseException
```

Maximum skips:

```java
1000
```

---

## Retry Logic

Retries transient failures:

```java
DeadlockLoserDataAccessException
CannotAcquireLockException
```

Retry limit:

```java
3
```

---

## Restartability

Spring Batch metadata tables track execution state.

If the application crashes:

- Successfully committed chunks are retained.
- Processing resumes from the last checkpoint.
- Duplicate processing is avoided.

---

## Logging

The application logs:

- SFTP connections.
- Downloaded files.
- Job launches.
- Step execution.
- Skip events.
- Retry attempts.
- Job completion status.

---

## Future Enhancements

- SSH private key authentication.
- Parallel step execution.
- Multi-file processing.
- Archive processed files.
- Move failed files to error directory.
- Email notifications.
- Micrometer metrics.
- OpenTelemetry tracing.
- Grafana dashboards.
- Kubernetes deployment.
- Spring Cloud Task integration.

---

## Expected Outcome

When a CSV file is uploaded to the SFTP server:

1. The poller detects the file.
2. The file is downloaded locally.
3. A Spring Batch job is launched.
4. Records are validated.
5. Invalid records are skipped.
6. Valid records are UPSERTED into PostgreSQL.
7. Execution metadata is stored.
8. Processing can restart safely after failures.
