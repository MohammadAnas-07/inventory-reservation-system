# Inventory & Reservation System

A production-grade backend service built with Java 21 and Spring Boot to manage inventory allocations and handle high-concurrency reservation workflows safely.

## Core Focus
- Concurrency control (Pessimistic vs. Optimistic Locking in PostgreSQL)
- Reservation state machine (PENDING, CONFIRMED, CANCELLED, EXPIRED)
- Idempotency in high-traffic endpoints
- Asynchronous domain event publishing using Apache Kafka
- Automated integration and race condition testing with Testcontainers