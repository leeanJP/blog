-- PUBLIC.ARTICLE definition
-- Drop table
-- DROP TABLE PUBLIC.ARTICLE;
CREATE TABLE PUBLIC.ARTICLE (
                                ID BIGINT NOT NULL AUTO_INCREMENT,
                                TITLE CHARACTER VARYING(255) NOT NULL,
                                CONTENT CHARACTER VARYING(3000) NOT NULL,
                                CREATED_AT TIMESTAMP,
                                UPDATED_AT TIMESTAMP,
                                CONSTRAINT CONSTRAINT_F PRIMARY KEY (ID)
);
CREATE UNIQUE INDEX PRIMARY_KEY_53 ON PUBLIC.ARTICLE (ID);


CREATE TABLE PUBLIC.USERS (
                              ID BIGINT NOT NULL AUTO_INCREMENT,
                              EMAIL VARCHAR(225) NOT NULL,
                              PASSWORD VARCHAR(255) NOT NULL,
                              CREATED_AT DATETIME NOT NULL,
                              UPDATED_AT DATETIME NOT NULL,
                              CONSTRAINT USERS_PK PRIMARY KEY (ID)
);

-- PUBLIC.REFRESHTOKEN definition

-- Drop table

-- DROP TABLE PUBLIC.REFRESHTOKEN;

CREATE TABLE PUBLIC.REFRESHTOKEN (
                                     ID BIGINT NOT NULL AUTO_INCREMENT,
                                     USER_ID BIGINT NOT NULL,
                                     REFRESH_TOKEN CHARACTER VARYING(255) NOT NULL,
                                     CONSTRAINT REFRESHTOKEN_PK PRIMARY KEY (ID)
);
CREATE UNIQUE INDEX PRIMARY_KEY_8 ON PUBLIC.REFRESHTOKEN (ID);