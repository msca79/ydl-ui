create sequence  "HIBERNATE_SEQUENCE" ;

create table "QUEUE" (
        "ID" number primary key,
        "KEY" varchar(255) unique,
        "URL" varchar(1024) unique,
        "STATUS" varchar(255),
        "FORMAT" varchar(255),
        "MESSAGE" varchar(1024),
        "CREATED_AT" timestamp,
        "DOWNLOADED_AT" timestamp,
    );
