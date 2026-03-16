CREATE TABLE performers (
                            id BIGSERIAL PRIMARY KEY,
                            name JSONB NOT NULL,
                            description JSONB,
                            image_url VARCHAR(500),
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stages (
                        id BIGSERIAL PRIMARY KEY,
                        name JSONB NOT NULL,
                        location_desc JSONB,
                        display_order INT NOT NULL DEFAULT 0,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE festival_days (
                               id BIGSERIAL PRIMARY KEY,
                               day_number INT NOT NULL,
                               event_date DATE NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT uq_festival_days_day_number UNIQUE (day_number),
                               CONSTRAINT uq_festival_days_event_date UNIQUE (event_date)
);

CREATE TABLE performance_schedules (
                                       id BIGSERIAL PRIMARY KEY,
                                       performer_id BIGINT NOT NULL,
                                       stage_id BIGINT NOT NULL,
                                       festival_day_id BIGINT NOT NULL,
                                       start_at TIMESTAMP NOT NULL,
                                       end_at TIMESTAMP NOT NULL,
                                       status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_performance_schedules_performer
                                           FOREIGN KEY (performer_id) REFERENCES performers(id),
                                       CONSTRAINT fk_performance_schedules_stage
                                           FOREIGN KEY (stage_id) REFERENCES stages(id),
                                       CONSTRAINT fk_performance_schedules_festival_day
                                           FOREIGN KEY (festival_day_id) REFERENCES festival_days(id),
                                       CONSTRAINT chk_performance_schedule_time
                                           CHECK (start_at < end_at)
);

CREATE TABLE favorites (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           performer_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_favorites_performer
                               FOREIGN KEY (performer_id) REFERENCES performers(id),
                           CONSTRAINT uq_favorites_user_performer UNIQUE (user_id, performer_id)
);

CREATE INDEX idx_performance_schedules_day ON performance_schedules(festival_day_id);
CREATE INDEX idx_performance_schedules_performer ON performance_schedules(performer_id);
CREATE INDEX idx_performance_schedules_stage ON performance_schedules(stage_id);
CREATE INDEX idx_performance_schedules_start_at ON performance_schedules(start_at);
CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorites_performer_id ON favorites(performer_id);
CREATE INDEX idx_performers_name_gin ON performers USING GIN (name);
CREATE INDEX idx_stages_name_gin ON stages USING GIN (name);