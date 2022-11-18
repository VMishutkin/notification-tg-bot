--liquibase formatted sql

--changeset vmish:1
CREATE TABLE notifications(
    id SERIAL,
    chat_id BIGINT,
    notification_text TEXT,
    notification_time TIMESTAMP
)