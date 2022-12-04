CREATE SEQUENCE IF NOT EXISTS booking_sequence START WITH 1 INCREMENT BY 5;

CREATE SEQUENCE IF NOT EXISTS candidate_slot_sequence START WITH 1 INCREMENT BY 5;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS interviewer_slot_sequence START WITH 1 INCREMENT BY 5;

CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 5;

CREATE TABLE IF NOT EXISTS candidate_time_slot
(
    id         BIGINT NOT NULL,
    email      VARCHAR(255),
    date_value date,
    from_time  TIME WITHOUT TIME ZONE,
    to_time    TIME WITHOUT TIME ZONE,
    CONSTRAINT pk_candidatetimeslot PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id          BIGINT NOT NULL,
    email       VARCHAR(255),
    role        VARCHAR(255),
    facebook_id BIGINT,
    first_name  VARCHAR(255),
    middle_name VARCHAR(255),
    last_name   VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS interviewer_time_slot
(
    id             BIGINT  NOT NULL,
    interviewer_id BIGINT,
    from_time      TIME WITHOUT TIME ZONE,
    to_time        TIME WITHOUT TIME ZONE,
    day_of_week    INTEGER,
    week_num       INTEGER NOT NULL,
    CONSTRAINT pk_interviewertimeslot PRIMARY KEY (id),
    CONSTRAINT FK_INTERVIEWERTIMESLOT_ON_INTERVIEWER FOREIGN KEY (interviewer_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id                  BIGINT NOT NULL,
    from_time           TIME WITHOUT TIME ZONE,
    to_time             TIME WITHOUT TIME ZONE,
    subject             VARCHAR(255),
    description         VARCHAR(4000),
    candidate_slot_id   BIGINT,
    interviewer_slot_id BIGINT,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ON_CANDIDATESLOT FOREIGN KEY (candidate_slot_id) REFERENCES candidate_time_slot (id),
    CONSTRAINT FK_BOOKING_ON_INTERVIEWERSLOT FOREIGN KEY (interviewer_slot_id) REFERENCES interviewer_time_slot (id)
);

CREATE TABLE IF NOT EXISTS booking_limit
(
    id             BIGINT  NOT NULL,
    interviewer_id BIGINT,
    week_num       INTEGER NOT NULL,
    limit_value    INTEGER NOT NULL,
    CONSTRAINT pk_bookinglimit PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON USERS (email);
CREATE INDEX IF NOT EXISTS idx_interviewer_slot_weeknum ON INTERVIEWER_TIME_SLOT (week_num);
CREATE INDEX IF NOT EXISTS idx_candidate_slot_date ON CANDIDATE_TIME_SLOT (date_value);
CREATE INDEX IF NOT EXISTS idx_candidate_email_in_slot on candidate_time_slot (email);